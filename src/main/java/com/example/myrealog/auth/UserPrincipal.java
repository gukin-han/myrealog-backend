package com.example.myrealog.auth;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long userId;
    private String email;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }

    public UserPrincipal(String email) {
        this.email = email;
    }
}
