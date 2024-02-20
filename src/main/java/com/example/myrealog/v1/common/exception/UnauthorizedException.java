package com.example.myrealog.v1.common.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException() {
        super("접근 권한이 없는 유저입니다.");
    }

}
