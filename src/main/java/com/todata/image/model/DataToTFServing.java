package com.todata.image.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DataToTFServing
 * @Author Zhen Lu
 * @Date 2020/11/25 11:24
 * @Function
 */
public class DataToTFServing<T> implements Serializable {
    public String signature_name = "serving_default";
    public List<T> instances;
}
