package com.example.myrealog.api.service.user.response;

import com.example.myrealog.domain.user.User;
import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class UserResponse {


    private Long id;
    private ProfileResponse profile;
    private String username;
    private String email;
    private String password;
    private LocalDateTime recentlyPublishedDate;

    @Builder
    private UserResponse(Long id, ProfileResponse profile, String username, String email, String password, LocalDateTime recentlyPublishedDate) {
        this.id = id;
        this.profile = profile;
        this.username = username;
        this.email = email;
        this.password = password;
        this.recentlyPublishedDate = recentlyPublishedDate;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .profile(ProfileResponse.of(user.getProfile()))
                .username(user.getUsername())
                .build();
    }
}
