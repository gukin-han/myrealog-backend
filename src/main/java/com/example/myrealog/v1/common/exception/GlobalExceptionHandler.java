package com.example.myrealog.v1.common.exception;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.v1.common.dto.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenExceptionException(InvalidTokenException ex) {
        final ResponseWrapper<?> generatedBody = new ResponseWrapper<>("문제가 발생했습니다. 다시 로그인해주세요.", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(generatedBody);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        final ResponseWrapper<?> generatedBody = new ResponseWrapper<>("요청한 유저를 찾을 수 없습니다.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generatedBody);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> bindException(BindException e) {
        return ApiResponse.of(false,
                HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                null
        );
    }


}
