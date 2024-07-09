package com.example.springbootsecurityauth.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(int statusCode, String error, String path) {
        this.timestamp = new Date();
        this.status = statusCode;
        this.error = error;
        this.path = path;
    }
}