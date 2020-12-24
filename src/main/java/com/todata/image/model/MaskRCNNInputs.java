package com.todata.image.model;

/**
 * @ClassName MaskRCNNInputs
 * @Author Zhen Lu
 * @Date 2020/11/26 13:11
 * @Function
 */
public class MaskRCNNInputs {
    private float[][][] inputAnchors;
    private float[][][][] inputImage;
    private float[][] inputImageMeta;

    public MaskRCNNInputs() {
    }

    public MaskRCNNInputs(float[][][] inputAnchors, float[][][][] inputImage, float[][] inputImageMeta) {
        this.inputAnchors = inputAnchors;
        this.inputImage = inputImage;
        this.inputImageMeta = inputImageMeta;
    }

    public float[][][] getInputAnchors() {
        return inputAnchors;
    }

    public void setInputAnchors(float[][][] inputAnchors) {
        this.inputAnchors = inputAnchors;
    }

    public float[][][][] getInputImage() {
        return inputImage;
    }

    public void setInputImage(float[][][][] inputImage) {
        this.inputImage = inputImage;
    }

    public float[][] getInputImageMeta() {
        return inputImageMeta;
    }

    public void setInputImageMeta(float[][] inputImageMeta) {
        this.inputImageMeta = inputImageMeta;
    }
}
