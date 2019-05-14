package com.thorough.core.modules.pathology.imglib.enums;

/**
 * Created by root on 7/7/17.
 */
public enum ReviewStatus {

    UNREVIEW("未审核"),
    INREVIEW("审核中"),
    REVIEWED("已审核"),
    FAILED("未通过");

    private String value;

    private ReviewStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
