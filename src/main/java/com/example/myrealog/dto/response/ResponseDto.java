package com.example.myrealog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto <T> {
    private Integer status;
    private String message;
    private T data;

    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(200, null, data);
    }

    public static ResponseDto<?> badRequest(String message) {
        return new ResponseDto<>(400, message, null);
    }

    public static ResponseDto<?> unauthorized(String message) {
        return new ResponseDto<>(401, message, null);
    }

}
