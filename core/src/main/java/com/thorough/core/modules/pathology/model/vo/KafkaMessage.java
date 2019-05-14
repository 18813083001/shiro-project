package com.thorough.core.modules.pathology.model.vo;


import java.io.Serializable;

public class KafkaMessage implements Serializable{

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
