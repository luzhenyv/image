package com.todata.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todata.image.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class ImageApplicationTests {

    @Test
    public void testMaskRCNN() throws IOException, InterruptedException {
        List<String> classNames = new ArrayList<>(Arrays.asList("BG", "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
                "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
                "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
                "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup",
                "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza",
                "donut", "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote",
                "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
                "teddy bear", "hair drier", "toothbrush"));
        // 读取图片
        RGBImage image = RGBImage.read("C:\\Users\\luzhe\\deep_learning\\maskrcnn\\images\\2.jpg");

        MaskRCNNConfig config = new MaskRCNNConfig();
        config.NUM_CLASSES = 81;
        config.TRAIN_ROIS_PER_IMAGE = 128;
        config.update();
        MaskRCNN model = new MaskRCNN(config);
        List<MaskRCNNResult> outputs = model.detect((RGBImage) image.clone());

        MaskRCNN.displayInstance(image, outputs.get(0), classNames);
        image.write("C:\\Users\\luzhe\\Desktop\\beautya.jpg", "JPG");

//        String str = Arrays.deepToString(result.getMasks().get(i));
//
//        try {
//            File newlog = new File("C:\\Users\\luzhe\\Desktop\\log.txt");
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newlog),"utf-8"));
//            for(int j=0; j<str.length(); j++){
//
//                bw.write(str.charAt(j));
//            }
//            bw.close();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }



    public RGBImage getRGB(Float[][][] prediction){
        Float[] positive = {255f, 0.f, 0.f};
        Float[] negative = {0.f, 255f, 0.f};
        Float[] other = {0.f, 0.f, 255f};
        Float[] background = {0.f, 0.f, 0.f};

        int height = prediction.length;
        int width = prediction[0].length;
        int channel = prediction[0][0].length;
        RGBImage image = new RGBImage(height, width);

        for(int h=0;h<height;h++)
            for (int w=0;w<width;w++){
                ArrayList<Float> arr = new ArrayList<>(Arrays.asList(prediction[h][w]));
                switch (arr.indexOf(Collections.max(arr))){
                    case 1:
                        image.setRGB(h,w,negative);
                        break;
                    case 2:
                        image.setRGB(h,w,other);
                        break;
                    case 3:
                        image.setRGB(h,w,positive);
                        break;
                    default:
                        image.setRGB(h,w,background);
                }
            }

        return image;
    }

    @Test
    public void testUnet() throws Exception {
        //读取图片
        BufferedImage bufImage = ImageIO.read(new File("C:\\Users\\luzhe\\deep_learning\\unet&hrnet\\data\\bloodcell\\test\\0.png"));
        RGBImage image = new RGBImage();
        image.bufImageToRGBImage(bufImage);
        image.divide(255f);

        float[][][][] instances = {image.getData()};

        String requestBody = "{\"instances\": "+ Arrays.deepToString(instances) +"}";

        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8501/v1/models/my_unet_model:predict"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());
        PredictionOfTFServing prediction = mapper.readValue(response.body(), PredictionOfTFServing.class);
        getRGB(prediction.getPredictions()[0]);

        ImageIO.write(getRGB(prediction.getPredictions()[0]).sRGBImageToBufImage(), "PNG", new File("C:\\Users\\luzhe\\deep_learning\\unet&hrnet\\data\\bloodcell\\test\\0_pre.png"));

    }

    @Test
    public void testreadMemoryImage() throws IllegalArgumentException, IOException {
        // 读取图片
        BufferedImage bufImage = ImageIO.read(new File("C:\\Users\\luzhe\\Pictures\\Camera Roll\\beauty.jpg"));

        // 获取图片的宽高
        final int width = bufImage.getWidth();
        final int height = bufImage.getHeight();

        // 读取出图片的所有像素
        int[] rgbs = bufImage.getRGB(0, 0, width, height, null, 0, width);

//        // 对图片的像素矩阵进行水平镜像
//        for (int row = 0; row < height; row++) {
//            for (int col = 0; col < width / 2; col++) {
//                int temp = rgbs[row * width + col];
//                rgbs[row * width + col] = rgbs[row * width + (width - 1 - col)];
//                rgbs[row * width + (width - 1 - col)] = temp;
//            }
//        }
//
//        // 把水平镜像后的像素矩阵设置回 bufImage
//        bufImage.setRGB(0, 0, width, height, rgbs, 0, width);
//
//        // 把修改过的 bufImage 保存到本地
//        ImageIO.write(bufImage, "JPEG", new File("C:\\Users\\luzhe\\Pictures\\Camera Roll\\beautya.jpg"));

        int[] rgb = {21, 22, 24};

        int color = bufImage.getRGB(10, 10);
        System.out.println("srgb is "+ color);
        System.out.println("s is "+ ((color & 0xff000000) >> 24));
        System.out.println("r is "+ ((color & 0xff0000) >> 16));
        System.out.println("g is "+ ((color & 0xff00) >> 8));
        System.out.println("s is "+ (color & 0xff));

    }

//    @Test
//    public void testTFServing() throws Exception {
//        List<Double> array = new ArrayList<>();
//        URL url = ClassLoader.getSystemResource("lib/opencv_java440.dll");
//        System.load(url.getPath());
//        Mat image = Imgcodecs.imread("C:\\Users\\luzhe\\deep_learning\\unet&hrnet\\data\\bloodcell\\test\\4.png", 1);
//        if (image.empty()){
//            throw new Exception("image is empty!");
//        }
//        // 图像行:高度height
//        int img_rows = image.rows();
//        // 图像列:宽度width
//        int img_colums = image.cols();
//        // 图像通道:维度dims/channels
//        int img_channels = image.channels();
//        System.out.println("image mat: " + image+"\n");
//        System.out.println("image rows: "+image.rows()+"\n");
//        System.out.println("image column: "+image.cols()+"\n");
//        System.out.println("image channels: "+image.channels()+"\n");
//        System.out.println("image value: "+image.get(0, 0).length+"\n");
//        // 图像像素遍历,按通道输出
//        for(int i=0;i<img_channels;i++) {
//            for(int j=0;j<img_rows;j++){
//                for(int k=0; k<img_colums;k++){
//                    array.add(image.get(j,k)[i]);
//                }
//            }
//            System.out.println("image value: "+array+"\n");
//            // 列表清空
//            array.clear();
//        }
//    }
//
//    @Test
//    public void testOpenCV() throws Exception {
//        URL url = ClassLoader.getSystemResource("lib/opencv_java440.dll");
//        System.load(url.getPath());
//        //填你的图片地址
//        Mat image = Imgcodecs.imread("C:\\Users\\luzhe\\Pictures\\Camera Roll\\beauty.jpg", 1);
//        if (image.empty()){
//            throw new Exception("image is empty!");
//        }
//        HighGui.imshow("Original Image", image);
//        List<Mat> imageRGB = new ArrayList<>();
//        Core.split(image, imageRGB);
//        for (int i = 0; i < 3; i++) {
//            Imgproc.equalizeHist(imageRGB.get(i), imageRGB.get(i));
//        }
//        Core.merge(imageRGB, image);
//        HighGui.imshow("Processed Image", image);
//        HighGui.waitKey();
//    }

    @Test
    void contextLoads() {
    }

}