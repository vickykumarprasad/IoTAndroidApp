package com.urjalabs.smartcontroller.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * to handle network response
 * Created by tarun on 01-11-2017.
 */

public class ResponseNode {
    @JsonProperty("urjaLabsException")
    private String urjaLabsException;
    @JsonProperty("httpStatusCode")
    private int httpStatusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private String data;

    public String getUrjaLabsException() {
        return urjaLabsException;
    }

    public void setUrjaLabsException(String urjaLabsException) {
        this.urjaLabsException = urjaLabsException;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
