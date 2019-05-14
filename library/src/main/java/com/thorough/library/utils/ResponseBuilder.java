package com.thorough.library.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseBuilder {
    public static final ResponseEntity<?> SUCCESS = new ResponseEntity<Object>(new ResponseWrapper(), HttpStatus.OK);
    public static final String DEFAULT_KEY = "data";
    private HttpStatus statusCode;
    private ResponseWrapper rw;

    private ResponseBuilder() {
        statusCode = HttpStatus.OK;
        rw = new ResponseWrapper();
    }

    public static ResponseBuilder newInstance() {
        return new ResponseBuilder();
    }

    public ResponseBuilder add(String key, Object value) {
        rw.add(key, value);
        return this;
    }

    public ResponseBuilder add(Object value) {
        rw.add(DEFAULT_KEY, value);
        return this;
    }

    public ResponseBuilder add(Map<String,Object> map){
        if(map != null){
            for(Map.Entry entry:map.entrySet())
                rw.add((String) entry.getKey(),entry.getValue());
        }
        return this;
    }

    public ResponseBuilder success() {
        this.rw.setSuccess(true);
        return this;
    }

    public ResponseBuilder error() {
        this.rw.setSuccess(false);
        return this;
    }

    public ResponseBuilder setWrapperCode(String code){
        this.rw.setCode(code);
        return this;
    }

    public ResponseBuilder code(HttpStatus statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseBuilder message(String message) {
        this.rw.setMessage(message);
        return this;
    }

    public ResponseEntity<?> build() {
        return new ResponseEntity<Object>(rw, statusCode);
    }

    @Override
    public String toString() {
        return "ResponseBuilder{" +
                "statusCode=" + statusCode +
                ", rw=" + rw +
                '}';
    }
}
