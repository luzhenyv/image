package com.todata.image.model;

/**
 * @ClassName PredictionOfTFServing
 * @Author Zhen Lu
 * @Date 2020/11/25 13:11
 * @Function
 */
public class PredictionOfTFServing {
    private Float predictions[][][][];

    public PredictionOfTFServing() {
    }

    public PredictionOfTFServing(Float[][][][] predictions) {
        this.predictions = predictions;
    }

    public Float[][][][] getPredictions() {
        return predictions;
    }

    public void setPredictions(Float[][][][] predictions) {
        this.predictions = predictions;
    }
}
