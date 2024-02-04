package com.example.myrealog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto <T> {
    private String message;
    private T data;

    public static <T> ResponseDto<T> of(T data) {
        return of("", data);
    }

    public static <T> ResponseDto<T> of(String message, T data) {
        return new ResponseDto<>(message, data);
    }
}
