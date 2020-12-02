package com.todata.image;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @ClassName MaskRCNN
 * @Author Zhen Lu
 * @Date 2020/11/26 13:48
 * @Function
 */
public class MaskRCNN {
    private MaskRCNNConfig config;
    private MaskRCNNOutputs outputs;
    private float[][][][] moldedImages;
    private float[][] imageMetas;
    private int[][] windows;
    private float[][][] anchors;

    private float[][][] moldedImage;
    private float[] imageMeta;
    private int[] window;
    private float scale;

    public static RGBImage displayInstance(RGBImage image, MaskRCNNResult result, List<String> classNames) {
        DecimalFormat df = new DecimalFormat("#.###");
        int y1, x1, y2, x2, numberOfInstances = result.getRois().size();
        String label;
        float score;

        System.out.println("numberOfInstances is " + numberOfInstances);

        if (numberOfInstances == 0)
            System.out.println("\n*** No instances to display *** \n");
        else
            assert result.getClassIDs().size() == result.getMasks().size();
        List<int[]> colors = randomColors(numberOfInstances);
        for (int i=0;i<numberOfInstances;i++){
            // Bounding box
            y1 = (int) result.getRois().get(i)[0];
            x1 = (int) result.getRois().get(i)[1];
            y2 = (int) result.getRois().get(i)[2];
            x2 = (int) result.getRois().get(i)[3];
            image.drawPolygon(new int[]{x1, x2, x2, x1}, new int[]{y1, y1, y2, y2}, 4, colors.get(i), 3, true);

            // Label
            label = classNames.get(result.getClassIDs().get(i));
            score = result.getScores().get(i);
            label = label + " " + df.format(score);
            int size = Math.max(image.getHeight() / 50, 20);
            image.drawLabel(1f, Font.DIALOG, Font.PLAIN, size, Color.RED, label, x1, y1+8);

            // Mask
            image.drawMask(result.getMasks().get(i), colors.get(i), 0.5f);
        }
        return image;

    }

    public static List<int[]> randomColors(Integer count) {
        SplittableRandom random = new SplittableRandom(47);
        List<int[]> colors = new ArrayList<>();
        int r,g,b;
        for (int i=0;i<count;i++){
            r = random.nextInt(0, 255);
            g = random.nextInt(0, 255);
            b = random.nextInt(0, 255);
            colors.add(new int[]{r, g, b});
        }
        return colors;
    }

    public MaskRCNN(MaskRCNNConfig config) {
        this.config = config;
    }

    public List<MaskRCNNResult> detect(RGBImage ...images) throws IOException, InterruptedException {
        List<RGBImage> originalImages = new ArrayList<>(Arrays.asList(images));
        moldInputs(originalImages);
        anchors = new float[1][][];
        anchors[0] = getAnchors(moldedImages[0].length, moldedImages[0][0].length);
        outputs = predict().getOutputs();
        return getResults(originalImages);
    }

    private List<MaskRCNNResult> getResults(List<RGBImage> images) {
        List<MaskRCNNResult> results = new ArrayList<>();
        for (int index=0;index<images.size();index++) {
            results.add(unmoldDetection(index));
        }
        return results;
    }

    private MaskRCNNResult unmoldDetection(Integer index) {
        MaskRCNNResult result = new MaskRCNNResult();
        List<float[]> boxes = new ArrayList<>();
        List<Integer> classIDs = new ArrayList<>();
        List<Float> scores = new ArrayList<>();
        List<float[][]> masks = new ArrayList<>();
        float x1, y1, x2, y2, wh, ww;
        float[] win;

        for(int i=0;i<outputs.getMrcnnDetection()[index].length;i++) {
            if (outputs.getMrcnnDetection()[index][i][4] > 0){

//                System.out.println("index is " + i);

                y1 = outputs.getMrcnnDetection()[index][i][0];
                x1 = outputs.getMrcnnDetection()[index][i][1];
                y2 = outputs.getMrcnnDetection()[index][i][2];
                x2 = outputs.getMrcnnDetection()[index][i][3];
                if ((x2-x1)*(y2-y1)>0){
                    classIDs.add((int) outputs.getMrcnnDetection()[index][i][4]);
                    scores.add(outputs.getMrcnnDetection()[index][i][5]);
                    masks.add(getMaskByClassID(outputs.getMrcnnMask()[index][i], (int) outputs.getMrcnnDetection()[index][i][4]));
                    boxes.add(new float[] {y1, x1, y2, x2});
                }
            }
        }

        win = normBoxes(windows[index], (int)imageMetas[index][4], (int)imageMetas[index][5]);
        wh = win[2] - win[0];
        ww = win[3] - win[1];

        for (float[] b : boxes) {
            b[0] = (b[0] - win[0]) / wh;
            b[1] = (b[1] - win[1]) / ww;
            b[2] = (b[2] - win[0]) / wh;
            b[3] = (b[3] - win[1]) / ww;
        }

        denormBoxes(boxes, (int) imageMetas[index][1], (int) imageMetas[index][2]);
        masks = unmoldMask(masks, boxes, (int)imageMetas[index][1], (int)imageMetas[index][2]);

        result.setClassIDs(classIDs);
        result.setMasks(masks);
        result.setRois(boxes);
        result.setScores(scores);

        return result;
    }

    private List<float[][]> unmoldMask(List<float[][]> masks, List<float[]> boxes, Integer height, Integer width) {
        int y1, x1, y2, x2;
        List<float[][]> fullMasks = new ArrayList<>();
        float[][] fullmask;
        int[][] mask;
        for(int i=0;i<masks.size();i++){
            fullmask = new float[height][width];
//            Arrays.fill(fullmask, 0.f);

            y1 = (int) boxes.get(i)[0];
            x1 = (int) boxes.get(i)[1];
            y2 = (int) boxes.get(i)[2];
            x2 = (int) boxes.get(i)[3];
            mask =  resizeMask(masks.get(i), y2-y1, x2-x1);

            for (int h=y1;h<y2;h++)
                for (int w=x1;w<x2;w++)
                    fullmask[h][w] = mask[h-y1][w-x1];

            fullMasks.add(fullmask);
        }
        return fullMasks;
    }

    private int[][] resizeMask(float[][] mask, Integer height, Integer width){
        RGBImage image = new RGBImage();
        image.maskToRGBImage(mask);
        image.resize(height,width);
        return image.sRGBImageToMAsk();
    }

    private void denormBoxes(List<float[]> boxes, Integer height, Integer width) {
        for (float[] b : boxes) {
            b[0] = b[0] * (height - 1) + 0;
            b[1] = b[1] * (width - 1) + 0;
            b[2] = b[2] * (height - 1) + 1;
            b[3] = b[3] * (width - 1) + 1;
        }
    }

    private float[][] getMaskByClassID(float[][][] masks, Integer classID) {
        float[][] mask = new float[masks.length][masks[0].length];
        for (int i=0;i<masks.length;i++)
            for (int j=0;j<masks[0].length;j++)
                mask[i][j] = masks[i][j][classID];
        return mask;
    }

    private Outputs predict() throws IOException, InterruptedException {
        String requestBody = "{\"signature_name\": \"serving_default\","
                + "\"inputs\": {" +
                "\"input_anchors\": " + Arrays.deepToString(anchors) +
                ", \"input_image\": "+ Arrays.deepToString(moldedImages) +
                ", \"input_image_meta\": "+ Arrays.deepToString(imageMetas) +
                "}}";

        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8501/v1/models/my_mask_rcnn_model:predict"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        mapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode root = mapper.readTree(response.body());
        return mapper.readValue(response.body(), Outputs.class);
    }

    private void moldInputs(List<RGBImage> images){
        int originalH, originalW, originalC, batchSize = images.size();
        moldedImages = new float[batchSize][][][];
        imageMetas = new float[batchSize][];
        windows = new int[batchSize][];

        for (int i=0;i<images.size();i++) {
            originalH = images.get(i).getHeight();
            originalW = images.get(i).getWidth();
            originalC = images.get(i).getChannel();

            resizeImage(images.get(i),
                    config.IMAGE_MIN_DIM,
                    config.IMAGE_MIN_SCALE,
                    config.IMAGE_MAX_DIM,
                    config.IMAGE_RESIZE_MODE);
            moldImage();

            //Build image_meta
            composeImageMeta(i, originalH, originalW, originalC);

            // Append
            moldedImages[i] = moldedImage;
            windows[i] = window;
            imageMetas[i] = imageMeta;
        }
    }

    private void resizeImage(RGBImage image, Integer minDim, Float minScale, Integer maxDim, String mode){
        int height = image.getHeight();
        int width = image.getWidth();
        int topPad, bottomPad, leftPad, rightPad;
        window = new int[] {0, 0, height, width};
        scale = 1;
        int[][] padding = new int[][]{{0, 0}, {0, 0}, {0, 0}};

        if (mode.equals("none"))
            return;
        if (minDim>0)
            scale = Math.max(1, (minDim/Math.min(height,width)));
        if (minScale>0 && scale<minScale)
            scale = minScale;

        // Does it exceed max dim?
        if (maxDim>0 && mode.equals("square")){
            int imageMax = Math.max(height, width);
            if (Math.round(imageMax*scale) > maxDim)
                scale = (float) maxDim / imageMax;
        }

        // Resize image using bilinear interpolation
        if (scale != 1)
            image.resize(Math.round(height*scale), Math.round(width*scale));

        // Need padding or cropping?
        if (mode.equals("square")){
            //  Get new height and width
            height = image.getHeight();
            width = image.getWidth();
            topPad = (maxDim - height) / 2;
            bottomPad = maxDim - height - topPad;
            leftPad = (maxDim - width) / 2;
            rightPad = maxDim - width - leftPad;

            padding = new int[][]{{topPad, bottomPad}, {leftPad, rightPad}, {0, 0}};
            image.pad(maxDim, maxDim, 0);
            window = new int[]{topPad, leftPad, height + topPad, width + leftPad};

        } else if (mode.equals("pad64")) {
            int maxH, maxW;
            height = image.getHeight();
            width = image.getWidth();

            if (height % 64 > 0){
                maxH = height - (height % 64) + 64;
                topPad = (maxH - height) / 2;
                bottomPad = maxH - height - topPad;
            } else {
                maxH = height;
                topPad = bottomPad = 0;
            }

            if (width % 64 > 0 ){
                maxW = width - (width % 64) + 64;
                leftPad = (maxW - width) / 2;
                rightPad = maxW - width - leftPad;
            } else {
                maxW = width;
                leftPad = rightPad = 0;
            }

            image.pad(maxH, maxW, 0);
            window = new int[]{topPad, leftPad, height + topPad, width + leftPad};
        } else if (mode.equals("crop")) {
            SplittableRandom random = new SplittableRandom(47);
            height = image.getHeight();
            width = image.getWidth();
            int y = random.nextInt(0, (height-minDim));
            int x = random.nextInt(0, (width-minDim));
            int[] crop = new int[]{y, x, maxDim, minDim};
            image.crop(y, x, height, width);
        } else
            throw new IllegalArgumentException("Mode " + mode + " not supported.");

        moldedImage = image.getData();
    }

    private void moldImage(){
        for (int h=0;h<moldedImage.length;h++)
            for (int w=0;w<moldedImage[0].length;w++) {
                moldedImage[h][w][0] -= config.MEAN_PIXEL.get(0);
                moldedImage[h][w][1] -= config.MEAN_PIXEL.get(1);
                moldedImage[h][w][2] -= config.MEAN_PIXEL.get(2);
            }
    }

    private void composeImageMeta(int index, int originalHeight, int originalWidth, int originalChannel){
        int size = 1 + 3 + 3 + 4 + 1 + config.NUM_CLASSES;
//        System.out.println(size);
        imageMeta = new float[size];
        Arrays.fill(imageMeta, 0);

        imageMeta[0] = index;

        imageMeta[1] = originalHeight;
        imageMeta[2] = originalWidth;
        imageMeta[3] = originalChannel;

        imageMeta[4] = moldedImage.length;
        imageMeta[5] = moldedImage[0].length;
        imageMeta[6] = moldedImage[0][0].length;

        imageMeta[7] = window[0];
        imageMeta[8] = window[1];
        imageMeta[9] = window[2];
        imageMeta[10] = window[3];

        imageMeta[11] = scale;
    }

    private float[][] getAnchors(Integer height, Integer width) {
        float[][] a = generatePyramidAnchors(height, width).toArray(new float[0][]);

        normBoxes(a, height, width);
        return a;
    }

    // scales: 1D array of anchor sizes in pixels. Example: [32, 64, 128]
    // ratios: 1D array of anchor ratios of width/height. Example: [0.5, 1, 2]
    // shape: [height, width] spatial shape of the feature map over which to generate anchors.
    // feature_stride: Stride of the feature map relative to the image in pixels.
    // anchor_stride: Stride of anchors on the feature map. For example, if the
    // value is 2 then generate anchors for every other feature map pixel.
    private List<float[]> generatePyramidAnchors(Integer height, Integer width) {

       List<Integer> scales = config.RPN_ANCHOR_SCALES;
       List<Float> ratios = config.RPN_ANCHOR_RATIOS;
       int[][] backboneShapes = computeBackboneShapes(height, width);
       List<Integer> featureStride = config.BACKBONE_STRIDES;
       int anchorStride = config.RPN_ANCHOR_STRIDE;

       List<float[]> a = new ArrayList<>();
       for (int i=0;i<scales.size();i++) {
           a.addAll(generateAnchors(scales.get(i), ratios, backboneShapes[i], featureStride.get(i), anchorStride));
       }

//       System.out.println(a);

       return a;
    }

    private int[][] computeBackboneShapes(Integer height, Integer width) {
        int[][] backboneShapes = new int[5][2];
        int[] strides = {4, 8, 16, 32, 64};

        for (int i=0;i<5;i++) {
            backboneShapes[i][0] = (int) Math.ceil(height / strides[i]);
            backboneShapes[i][1] = (int) Math.ceil(width / strides[i]);
        }
        return backboneShapes;
    }

    private List<float[]> generateAnchors(int scales, List<Float> ratios, int[] backboneShapes, int featureStride, int anchorStride) {
        List<float[]> a = new ArrayList<>();
        float x1, y1, x2, y2; // anchor的左上，右下点坐标
        List<Float> heights = new ArrayList<>();
        List<Float> widths = new ArrayList<>();
        List<Integer> shiftY = IntStream.range(0, backboneShapes[0]).filter(n -> n % anchorStride == 0).map(n -> n * featureStride).boxed().collect(Collectors.toList());
        List<Integer> shiftX = IntStream.range(0, backboneShapes[1]).filter(n -> n % anchorStride == 0).map(n -> n * featureStride).boxed().collect(Collectors.toList());
//        System.out.println(" shiftY is " + shiftY);
//        System.out.println(" shiftX is " + shiftX);


        for (float r : ratios){
            heights.add((float) (scales/Math.sqrt(r)));
            widths.add((float) (scales*Math.sqrt(r)));
        }

        for (int y:shiftY)
            for (int x:shiftX)
                for(int i=0;i<ratios.size();i++) {
                    y1 = (float) (y - 0.5 * heights.get(i));
                    y2 = (float) (y + 0.5 * heights.get(i));
                    x1 = (float) (x - 0.5 * widths.get(i));
                    x2 = (float) (x + 0.5 * widths.get(i));
                    a.add(new float[] {y1, x1, y2, x2});
                }
        return a;
    }

    private float[][] normBoxes(float[][] boxes, Integer height, Integer width) {
        height -= 1;
        width -= 1;
        for (int i=0;i<boxes.length;i++) {
            boxes[i][0] /= height;
            boxes[i][1] /= width;
            boxes[i][2] -= 1;
            boxes[i][2] /= height;
            boxes[i][3] -= 1;
            boxes[i][3] /= width;
        }
        return boxes;
    }

    private float[] normBoxes(int[] boxes, Integer height, Integer width) {
        float[] b = new float[boxes.length];
        height -= 1;
        width -= 1;

        b[0] = (float) boxes[0] / height;
        b[1] = (float) boxes[1] / width;
        b[2] = boxes[2] - 1;
        b[2] /= height;
        b[3] = boxes[3] - 1;
        b[3] /= width;

        return b;
    }
}
