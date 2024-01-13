package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "articles")
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "article", cascade = {REMOVE}, orphanRemoval = true)
    private List<ArticleAttachment> articleAttachments = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = {REMOVE}, orphanRemoval = true)
    private List<ArticleReaction> articleReactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "article", cascade = {REMOVE}, orphanRemoval = true)
    private List<Discussion> discussions = new ArrayList<>();

    private String title;
    private String content;
    private String slug;
    private String thumbnailUrl;
    private String excerpt;
    private int reactionCount;
    private int discussionCount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private enum Status {
        PRIVATE, PUBLIC, DRAFT
    }
}
