package com.sharkapi.web.uiservice.pojo;

import java.io.Serializable;
import java.util.Date;

public class ResultModel<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public ResultModel() {
    }

    public ResultModel(int code, String message, T data, long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public ResultModel(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = new Date().getTime();
    }

    /* 调用成功
    * */
    public ResultModel(String message, T data) {
        this.code = 100;
        this.message = message;
        this.data = data;
        this.timestamp = new Date().getTime();
    }


}
