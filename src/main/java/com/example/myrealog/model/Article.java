package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    private String slug;
    private String thumbnailUrl;
    private String excerpt;
    private int reactionCount;
    private int discussionCount;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Article(String title, String content, String excerpt, Status status) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.excerpt = excerpt;
        this.slug = generateSlug(title);
        this.thumbnailUrl = "";
    }

    public enum Status {
        PRIVATE, PUBLIC, DRAFT
    }

    public void setAuthor(User user) {
        this.user = user;
    }

    private String generateSlug(String title) {
        String slug = title
                        .replaceAll("[^a-zA-Z0-9가-힣\\-\\s]", "")
                        .replaceAll("\\s+", "-");

        String randomString = UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 8);

        return slug + "-" + randomString;
    }
}
