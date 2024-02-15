package com.example.myrealog.v1.model;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
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

    private LocalDateTime recentlyPublishedDate;

    @Builder
    private User(Profile profile, String username, String email) {
        this.profile = profile;
        this.username = username;
        this.email = email;
    }

    public static User create(String email, String username, String displayName, String bio) {
        return new User(Profile.of(displayName, bio), username, email);
    }

    public void updateRecentlyPublishedDate(LocalDateTime date) {
        recentlyPublishedDate = date;
    }

    public void updateProfile(Profile profile) {
        this.profile = profile;
        profile.updateUser(this);
    }
}
