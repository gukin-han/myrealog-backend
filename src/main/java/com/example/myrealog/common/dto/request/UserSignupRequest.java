package com.example.myrealog.common.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSignupRequest {

    private final String email;
    private final String username;
    private final String displayName;
    private final String bio;

    @Builder
    private UserSignupRequest(String email, String username, String displayName, String bio) {
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
    }
}
