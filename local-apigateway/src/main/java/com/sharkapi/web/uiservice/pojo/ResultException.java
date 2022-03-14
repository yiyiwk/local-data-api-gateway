package com.sharkapi.web.uiservice.pojo;

public class ResultException extends Exception{

    private Integer state = 0;
    private String message = "";

    public ResultException(Integer state, String message) {
        super(message);
        this.state = state;
        this.message = message;
    }

    public ResultException(Integer state) {
        super("");
        this.state = state;
    }

    public Integer getState() {
        return this.state;
    }

    public String getMessage() {
        return this.message;
    }
}
