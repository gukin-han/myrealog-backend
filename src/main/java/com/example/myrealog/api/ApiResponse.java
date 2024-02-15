package com.example.myrealog.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
public class ApiResponse<T> {

    private boolean isSuccess;
    private HttpStatus httpStatus;
    private String message;
    private T data;

    public ApiResponse(boolean isSuccess, HttpStatus httpStatus, String message, T data) {
        this.isSuccess = isSuccess;
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(Boolean isSuccess, HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(isSuccess, httpStatus, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(true, OK, "", data);
    }
}