package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = LAZY, cascade = {REMOVE}, orphanRemoval = true)
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

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

}
