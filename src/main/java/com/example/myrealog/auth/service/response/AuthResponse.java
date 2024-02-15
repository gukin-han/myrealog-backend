package com.example.myrealog.auth.service.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class AuthResponse {
    private Type type;
    private String value;

    @Builder
    private AuthResponse(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static AuthResponse of(Type type, String jwt) {
        return new AuthResponse(type, jwt);
    }

    @RequiredArgsConstructor
    public enum Type {
        ACCESS_TOKEN("accessToken"), SIGNUP_TOKEN("signupToken");

        private final String name;
    }
}
