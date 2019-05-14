package com.thorough.core.modules.pathology.imglib.enums;

/**
 * Created by root on 7/7/17.
 */
public enum ImageType {
    KFB("kfb"),
    TIF("tif");

    private String value;

    private ImageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
