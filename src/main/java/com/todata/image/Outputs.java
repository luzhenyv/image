package com.todata.image;

import java.io.Serializable;

/**
 * @ClassName Outputs
 * @Author Zhen Lu
 * @Date 2020/11/26 13:04
 * @Function
 */
public class Outputs implements Serializable {
    private MaskRCNNOutputs outputs;

    public Outputs() {
    }

    public Outputs(MaskRCNNOutputs outputs) {
        this.outputs = outputs;
    }

    public MaskRCNNOutputs getOutputs() {
        return outputs;
    }

    public void setOutputs(MaskRCNNOutputs outputs) {
        this.outputs = outputs;
    }

    @Override
    public String toString() {
        return "Outputs{" +
                "outputs=" + outputs +
                '}';
    }
}
