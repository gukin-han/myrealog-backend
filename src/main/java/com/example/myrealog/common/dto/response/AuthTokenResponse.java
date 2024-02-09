package com.example.myrealog.common.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class AuthTokenResponse {
    private Type type;
    private String value;

    @Builder
    private AuthTokenResponse(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static AuthTokenResponse of(Type type, String jwt) {
        return new AuthTokenResponse(type, jwt);
    }

    @RequiredArgsConstructor
    public enum Type {
        ACCESS_TOKEN("accessToken"), SIGNUP_TOKEN("signupToken");

        private final String name;
    }
}
