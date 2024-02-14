package com.example.myrealog.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Profile extends BaseTimeEntity{

    @Id
    @GeneratedValue
    @Column(name = "profile_id")
    private Long id;

    @OneToMany(mappedBy = "profile", cascade = {REMOVE}, orphanRemoval = true)
    private List<SocialChannel> socialChannels = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "profile")
    @JoinColumn(nullable = false)
    private User user;

    private String displayName;
    private String avatarUrl;
    private String bio;

    @Builder
    private Profile(String displayName, String bio, String avatarUrl) {
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public static Profile of(String displayName, String bio) {
        return new Profile(displayName, bio, "");
    }

    public void updateUser(User user) {
        this.user = user;
    }

}
