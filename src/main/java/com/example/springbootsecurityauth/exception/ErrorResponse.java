package com.example.springbootsecurityauth.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {
    private Date timestamp;
    private int statusCode;
    private String message;

    public ErrorResponse(int statusCode, String message) {
        this.timestamp = new Date();
        this.statusCode = statusCode;
        this.message = message;
    }
}