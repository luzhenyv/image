package com.todata.image.model;

/**
 * @ClassName Inputs
 * @Author Zhen Lu
 * @Date 2020/11/26 13:02
 * @Function
 */
public class Inputs<T> {
    private T inputs;

    public Inputs() {
    }

    public Inputs(T inputs) {
        this.inputs = inputs;
    }

    public T getInputs() {
        return inputs;
    }

    public void setInputs(T inputs) {
        this.inputs = inputs;
    }
}
