package com.thorough.core.modules.pathology.imglib.enums;

/**
 * Created by root on 7/7/17.
 */
public enum  Gender {
    FEMALE("女"),
    MALE("男"),
    UNKNOWN("未知");

    private String value;

    private Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
