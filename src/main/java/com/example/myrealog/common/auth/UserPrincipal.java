package com.example.myrealog.common.auth;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long userId;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }
}
