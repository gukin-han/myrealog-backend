package com.example.myrealog.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
public class ApiResponse<T> {

    private int code;
    private HttpStatus httpStatus;
    private String message;
    private T data;

    public ApiResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(Boolean isSuccess, HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(true, OK, "", data);
    }
}