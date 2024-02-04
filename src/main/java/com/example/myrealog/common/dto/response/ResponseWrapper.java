package com.example.myrealog.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseWrapper<T> {
    private String message;
    private T data;

    public static <T> ResponseWrapper<T> of(T data) {
        return of("", data);
    }

    public static <T> ResponseWrapper<T> of(String message, T data) {
        return new ResponseWrapper<>(message, data);
    }
}
