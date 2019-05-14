package com.thorough.core.modules.pathology.imglib.enums;

public enum OperatedType {
    PATIENT("病人"),
    DOCTOR("医生"),
    IMAGE("病理切片"),
    HOSPITAL("医院"),
    STAMP("标注");

    private String value;

    private OperatedType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
