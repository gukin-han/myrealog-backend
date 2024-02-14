package com.example.myrealog.v1.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "social_channels")
public class SocialChannel extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "social_channel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String url;

    private enum Type {
        GITHUB, LINKEDIN, YOUTUBE
    }
}
