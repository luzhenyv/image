package com.todata.image;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @ClassName MaskRCNNOutputs
 * @Author Zhen Lu
 * @Date 2020/11/26 13:25
 * @Function
 */
public class MaskRCNNOutputs implements Serializable {
//    @JsonIgnore
//    private float[][][] ROI;
    private float[][][][] mrcnnBbox;
    private float[][][] mrcnnClass;
    private float[][][] mrcnnDetection;
    private float[][][][][] mrcnnMask;
    private float[][][] rpnBbox;
    private float[][][] rpnClass;

    public MaskRCNNOutputs() {
    }

    public MaskRCNNOutputs(float[][][][] mrcnnBbox, float[][][] mrcnnClass, float[][][] mrcnnDetection, float[][][][][] mrcnnMask, float[][][] rpnBbox, float[][][] rpnClass) {
//        this.ROI = ROI;
        this.mrcnnBbox = mrcnnBbox;
        this.mrcnnClass = mrcnnClass;
        this.mrcnnDetection = mrcnnDetection;
        this.mrcnnMask = mrcnnMask;
        this.rpnBbox = rpnBbox;
        this.rpnClass = rpnClass;
    }

    public float[][][][] getMrcnnBbox() {
        return mrcnnBbox;
    }

    public void setMrcnnBbox(float[][][][] mrcnnBbox) {
        this.mrcnnBbox = mrcnnBbox;
    }

    public float[][][] getMrcnnClass() {
        return mrcnnClass;
    }

    public void setMrcnnClass(float[][][] mrcnnClass) {
        this.mrcnnClass = mrcnnClass;
    }

    public float[][][] getMrcnnDetection() {
        return mrcnnDetection;
    }

    public void setMrcnnDetection(float[][][] mrcnnDetection) {
        this.mrcnnDetection = mrcnnDetection;
    }

    public float[][][][][] getMrcnnMask() {
        return mrcnnMask;
    }

    public void setMrcnnMask(float[][][][][] mrcnnMask) {
        this.mrcnnMask = mrcnnMask;
    }

    public float[][][] getRpnBbox() {
        return rpnBbox;
    }

    public void setRpnBbox(float[][][] rpnBbox) {
        this.rpnBbox = rpnBbox;
    }

    public float[][][] getRpnClass() {
        return rpnClass;
    }

    public void setRpnClass(float[][][] rpnClass) {
        this.rpnClass = rpnClass;
    }

    @Override
    public String toString() {
        return "MaskRCNNOutputs{" +
//                "ROI=" + Arrays.toString(ROI) +
                ", mrcnnBbox=" + Arrays.toString(mrcnnBbox) +
                ", mrcnnClass=" + Arrays.toString(mrcnnClass) +
                ", mrcnnDetection=" + Arrays.toString(mrcnnDetection) +
                ", mrcnnMask=" + Arrays.toString(mrcnnMask) +
                ", rpnBbox=" + Arrays.toString(rpnBbox) +
                ", rpnClass=" + Arrays.toString(rpnClass) +
                '}';
    }
}
