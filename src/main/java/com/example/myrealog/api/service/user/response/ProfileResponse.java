package com.example.myrealog.api.service.user.response;

import com.example.myrealog.domain.user.Profile;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileResponse {

    private Long id;
    private String displayName;
    private String avatarUrl;
    private String bio;

    @Builder
    public ProfileResponse(Long id, String displayName, String avatarUrl, String bio) {
        this.id = id;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
    }

    public static ProfileResponse of(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .build();
    }
}
