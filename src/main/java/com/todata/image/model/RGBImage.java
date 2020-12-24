package com.todata.image.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

/**
 * @ClassName RGBImage
 * @Author Zhen Lu
 * @Date 2020/11/25 10:52
 * @Function
 */
public class RGBImage implements Cloneable{

    public static final String GIF = "GIF";
    public static final String PNG = "PNG";
    public static final String JPG = "JPG";
    public static final String JPEG = "JPEG";
    public static final String BMP = "BMP";
    public static final String WBMP = "WBMP";
    public static final String TIF = "TIF";
    public static final String TIFF = "TIFF";

    private Integer height;
    private Integer width;
    private Integer channel = 3;
    private String type = "RGB";
    private float[][][] data;

    public RGBImage() {
    }

    public RGBImage(Integer height, Integer width) {
        this.height = height;
        this.width = width;
        this.data = new float[this.height][this.width][this.channel];
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float[][][] getData() {
        return data;
    }

    public void setData(float[][][] data) {
        this.data = data;
    }

    public void setRGB(Integer h, Integer w, Float[] rgb){
        data[h][w][0] = rgb[0];
        data[h][w][1] = rgb[1];
        data[h][w][2] = rgb[2];
    }

    @Override
    public Object clone() {
        RGBImage image = null;
        try {
            image = (RGBImage) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return image;
    }

    /* 转换 */
    public void bufImageToRGBImage(BufferedImage bufImage) {
        this.height = bufImage.getHeight();
        this.width = bufImage.getWidth();
        this.data = new float[height][width][channel];
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++){
                int color = bufImage.getRGB(w,h);
//                System.out.println("color is "+ color);
                int[] sRGB = colorToRGB(color);
                data[h][w][0] = (float) sRGB[1];
                data[h][w][1] = (float) sRGB[2];
                data[h][w][2] = (float) sRGB[3];
            }
    }

    private int[] colorToRGB(int color){
        int[] srgb = new int[4];
        srgb[0] = (color & 0xff000000) >> 24;
        srgb[1] = (color & 0xff0000) >> 16;
        srgb[2] = (color & 0xff00) >> 8;
        srgb[3] = color & 0xff;
        return srgb;
    }

    public BufferedImage sRGBImageToBufImage(){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++) {
                image.setRGB(w,h, sRGBToColor(data[h][w]));
            }
        return image;
    }
    private int sRGBToColor(float[] srgb) {
        int r = (int) srgb[0];
        int g = (int) srgb[1];
        int b = (int) srgb[2];
        return (255 << 24) | (r << 16) | (g<< 8) | (b);
    }

    public void maskToRGBImage(float[][] mask){
        this.height = mask.length;
        this.width = mask[0].length;
        this.data = new float[height][width][channel];

        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++){
                data[h][w][0] = 255*mask[h][w];
                data[h][w][1] = 255*mask[h][w];
                data[h][w][2] = 255*mask[h][w];
            }
    }

    public int[][] sRGBImageToMAsk(){
        int[][] mask = new int[height][width];
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++)
                mask[h][w] = (int) Math.min(data[h][w][0]/128, 1);
        return mask;
    }

    /* 四则运算 */
    public void add(Float number) {
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++)
                for(int c=0;c<channel;c++) {
                    data[h][w][c] += number;
                }
    }

    public void reduce(Float number) {
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++)
                for(int c=0;c<channel;c++) {
                    data[h][w][c] -= number;
                }
    }

    public void multiply(Float number) {
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++)
                for(int c=0;c<channel;c++) {
                    data[h][w][c] *= number;
                }
    }

    public void divide(Float number) {
        for (int h=0;h<height;h++)
            for(int w=0;w<width;w++)
                for(int c=0;c<channel;c++) {
                    data[h][w][c] /= number;
                }
    }

    /*读取图片*/
    public static RGBImage read(String pathname) throws IOException {
        BufferedImage bufImage = ImageIO.read(new File(pathname));
        RGBImage image = new RGBImage();
        image.bufImageToRGBImage(bufImage);
        return image;
    }

    /*保存图片*/
    public static void write(RGBImage image, String pathname, String formatName) throws IOException {
        ImageIO.write(image.sRGBImageToBufImage(), formatName, new File(pathname));
    }

    public void write(String pathname, String formatName) throws IOException {
        ImageIO.write(this.sRGBImageToBufImage(), formatName, new File(pathname));
    }

    /*重置图形的边长大小*/
    public void resize(Integer height, Integer width) {
        BufferedImage src = sRGBImageToBufImage();
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) tag.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(src, 0, 0, width, height, null);
        graphics2D.dispose();
        bufImageToRGBImage(tag);
    }

    /*四周填充 padding*/
    public void pad(Integer height, Integer width, Integer constant) {
        BufferedImage src = sRGBImageToBufImage();
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] rgbs = new int[height*width];

        Arrays.fill(rgbs, sRGBToColor(new float[] {constant, constant, constant}));
        tag.setRGB(0, 0, width, height, rgbs, 0, width);

        tag.getGraphics().drawImage(src,
                tag.getWidth()/2-src.getWidth()/2,
                tag.getHeight()/2-src.getHeight()/2,
                null);
        bufImageToRGBImage(tag);
    }

    /*裁剪*/
    public void crop(Integer y, Integer x, Integer height, Integer width) {
        //使用ImageIO的read方法读取图片
        BufferedImage src = sRGBImageToBufImage();
        //调用裁剪方法
        BufferedImage tag = src.getSubimage(x, y, width, height);
        bufImageToRGBImage(tag);
    }

    /*绘制*/
    /**
     * 绘制一个由 x 和 y 坐标数组定义的闭合多边形
     * @param xPoints	x坐标数组
     * @param yPoints	y坐标数组
     * @param nPoints	坐标点的个数
     * @param polygonColor	线条颜色
     * @throws IOException
     */
    public void drawPolygon(int[] xPoints,int[] yPoints, int nPoints, int[] polygonColor, int lineWidth, boolean dash){
        try {
            //获取图片
            BufferedImage image = sRGBImageToBufImage();
            //根据xy点坐标绘制闭合多边形
            Graphics2D g2d = image.createGraphics();
            Stroke stroke = new BasicStroke(lineWidth);

            if (dash)
                stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

            g2d.setStroke(stroke);
            g2d.setColor(new Color(polygonColor[0], polygonColor[1], polygonColor[2]));
            g2d.drawPolygon(xPoints, yPoints, nPoints);
            g2d.dispose();
            bufImageToRGBImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在源图片上设置水印文字
     * @param alpha	透明度（0<alpha<1）
     * @param font	字体（例如：宋体）
     * @param fontStyle		字体格式(例如：普通样式--Font.PLAIN、粗体--Font.BOLD )
     * @param fontSize	字体大小
     * @param color	字体颜色(例如：黑色--Color.BLACK)
     * @param inputWords		输入显示在图片上的文字
     * @param x		文字显示起始的x坐标
     * @param y		文字显示起始的y坐标
     * @throws IOException
     */
    public void drawLabel(float alpha, String font,int fontStyle,int fontSize, Color color,
                          String inputWords,int x,int y) {
        try {
            //获取图片
            BufferedImage image = sRGBImageToBufImage();
            //创建java2D对象
            Graphics2D g2d=image.createGraphics();
            //用源图像填充背景
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null, null);
            //设置透明度
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(ac);
            //设置文字字体名称、样式、大小
            g2d.setFont(new Font(font, fontStyle, fontSize));
            g2d.setColor(color);//设置字体颜色
            g2d.drawString(inputWords, x, y); //输入水印文字及其起始x、y坐标
            g2d.dispose();
            bufImageToRGBImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 绘制mask
    public void drawMask(float[][] mask, int[] color, float alpha){
        for (int h=0;h<height;h++)
            for (int w=0;w<width;w++)
                if (mask[h][w]>0.5)
                    for (int c=0;c<channel;c++)
                        data[h][w][c] = (1 - alpha) * data[h][w][c] + alpha * color[c];
    }
}
