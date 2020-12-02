package com.todata.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @ClassName ImageController
 * @Author Zhen Lu
 * @Date 2020/12/2 12:07
 * @Function
 */
@RestController
@RequestMapping(value = "/image")
public class ImageController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping(value = "/upload")
    public Map<String,Object> upload(@RequestParam(value="file", required=false) MultipartFile file) throws Exception {
        BufferedImage image = null;
        Map<String, Object> map = new HashMap<>();
        // 保存
        if (file.isEmpty())
            throw new Exception();
        image = ImageIO.read(file.getInputStream());
        save(file);
//         返回
        map.put("code",0);
        map.put("msg","");
        map.put("data", process(image));
        return map;
    }

    private void save(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        assert originalName != null;
        String suffix = originalName.substring(originalName.lastIndexOf(".")+1);
        String dateStr = String.valueOf(System.currentTimeMillis());

        String filepath = "D:\\imagedir\\data\\" + dateStr+ "." + suffix;

        File f = new File(filepath);

        //打印查看上传路径
        System.out.println(filepath);
        if(!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        file.transferTo(f);
    }

    private String process(BufferedImage image) throws IOException, InterruptedException {
        List<String> classNames = new ArrayList<>(Arrays.asList("BG", "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
                "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
                "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
                "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup",
                "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza",
                "donut", "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote",
                "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
                "teddy bear", "hair drier", "toothbrush"));
        // 读取图片
        RGBImage img = new RGBImage();
        img.bufImageToRGBImage(image);

        MaskRCNNConfig config = new MaskRCNNConfig();
        config.NUM_CLASSES = 81;
        config.TRAIN_ROIS_PER_IMAGE = 128;
        config.update();
        MaskRCNN model = new MaskRCNN(config);
        List<MaskRCNNResult> outputs = model.detect((RGBImage) img.clone());

        MaskRCNN.displayInstance(img, outputs.get(0), classNames);

        BufferedImage bimg = img.sRGBImageToBufImage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bimg, "PNG", outputStream);
        String base64Img = Base64.getEncoder().encodeToString(outputStream.toByteArray());

//        String str = "data:image/png;base64," + base64Img;
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

        return "data:image/png;base64," + base64Img;

    }


}
