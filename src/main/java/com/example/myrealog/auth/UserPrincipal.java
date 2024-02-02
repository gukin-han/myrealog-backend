package com.example.myrealog.auth;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long userId;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }
}
