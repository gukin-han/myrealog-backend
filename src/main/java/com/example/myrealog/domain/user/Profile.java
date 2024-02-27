package com.example.myrealog.domain.user;

import com.example.myrealog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@Table(name = "profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "profile_id")
    private Long id;

    private String displayName;
    private String avatarUrl;
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String displayEmail;

    @Builder
    private Profile(String displayName, String bio, String avatarUrl) {
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public static Profile of(String displayName, String bio) {
        return Profile.builder()
                .displayName(displayName)
                .bio(bio)
                .avatarUrl("")
                .build();
    }
}
