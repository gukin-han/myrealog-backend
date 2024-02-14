package com.example.myrealog.v1.common.auth;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long userId;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }
}
