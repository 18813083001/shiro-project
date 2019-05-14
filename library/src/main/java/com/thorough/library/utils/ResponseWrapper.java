package com.thorough.library.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseWrapper {
    private boolean success = true;
    private String message = "";
    private String code = "";
    private Map<String, Object> data = new HashMap<String, Object>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean status) {
        this.success = status;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void add(String key, Object value) {
        data.put(key, value);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
