package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@Table(name = "profiles")
public class Profile extends BaseTimeEntity{

    @Id
    @GeneratedValue
    @Column(name = "profile_id")
    private Long id;

    @OneToMany(mappedBy = "profile", cascade = {REMOVE}, orphanRemoval = true)
    private List<SocialChannel> socialChannels = new ArrayList<>();

    @OneToOne(mappedBy = "profile")
    @JoinColumn(nullable = false)
    private User user;

    private String displayName;
    private String avatarUrl;
    private String bio;

}
