package com.todata.image;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MaskRCNNResult
 * @Author Zhen Lu
 * @Date 2020/11/30 11:42
 * @Function
 */
public class MaskRCNNResult {
    private List<float[]> rois;
    private List<float[][]> masks;
    private List<Integer> classIDs;
    private List<Float> scores;

    public MaskRCNNResult() {
    }

    public MaskRCNNResult(List<float[]> rois, List<float[][]> masks, List<Integer> classIDs, List<Float> scores) {
        this.rois = rois;
        this.masks = masks;
        this.classIDs = classIDs;
        this.scores = scores;
    }

    public List<float[]> getRois() {
        return rois;
    }

    public void setRois(List<float[]> rois) {
        this.rois = rois;
    }

    public List<float[][]> getMasks() {
        return masks;
    }

    public void setMasks(List<float[][]> masks) {
        this.masks = masks;
    }

    public List<Integer> getClassIDs() {
        return classIDs;
    }

    public void setClassIDs(List<Integer> classIDs) {
        this.classIDs = classIDs;
    }

    public List<Float> getScores() {
        return scores;
    }

    public void setScores(List<Float> scores) {
        this.scores = scores;
    }


    @Override
    public String toString() {
        return "MaskRCNNResult{" +
                "rois=" + rois +
                ", masks=" + masks +
                ", classIDs=" + classIDs +
                ", scores=" + scores +
                '}';
    }
}
