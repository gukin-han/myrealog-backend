package com.example.myrealog.v1.common.exception;

public class DiscussionNotFoundException extends RuntimeException {
    public DiscussionNotFoundException() {
        super("존재하지 않는 디스커션입니다.");
    }
}
