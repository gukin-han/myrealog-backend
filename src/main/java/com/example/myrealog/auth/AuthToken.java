package com.example.myrealog.auth;

import lombok.Data;

@Data
public class AuthToken {
    private Type type;
    private String value;

    public AuthToken(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum Type {
        ACCESS_TOKEN, SIGNUP_TOKEN;
    }

}
