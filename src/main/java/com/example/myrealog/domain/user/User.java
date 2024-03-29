package com.example.myrealog.domain.user;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.v1.model.ArticleReaction;
import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.v1.model.DiscussionReaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = {REMOVE}, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {REMOVE}, orphanRemoval = true)
    private List<ArticleReaction> articleReactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {REMOVE},orphanRemoval = true)
    private List<Discussion> discussions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {REMOVE}, orphanRemoval = true)
    private List<DiscussionReaction> discussionReactions = new ArrayList<>();

    @Column(unique = true, nullable = false)
    private String username;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDateTime recentlyPublishedDateTime;

    @Builder
    private User(Profile profile, String username, String email, LocalDateTime recentlyPublishedDateTime) {
        this.profile = profile;
        this.username = username;
        this.email = email;
        this.recentlyPublishedDateTime = recentlyPublishedDateTime;
    }

    public static User of(String email, String username, String displayName, String bio, LocalDateTime now) {
        return User.builder()
                .email(email)
                .username(username)
                .profile(Profile.of(displayName, bio))
                .recentlyPublishedDateTime(now)
                .build();
    }

    public void updateRecentlyPublishedDateTime(LocalDateTime date) {
        recentlyPublishedDateTime = date;
    }
}
