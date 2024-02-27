package com.example.myrealog.domain.article;

import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.v1.model.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    private String slug;
    private String thumbnailUrl;
    private String excerpt;
    private int reactionCount;
    private int discussionCount;

    @Enumerated(EnumType.STRING)
    private ArticleStatus articleStatus;

    private LocalDateTime publishedDataTime;

    @Builder
    private Article(Long id, User user, Category category, List<ArticleAttachment> articleAttachments, List<ArticleReaction> articleReactions, Topic topic, List<Discussion> discussions, String title, String content, String slug, String thumbnailUrl, String excerpt, int reactionCount, int discussionCount, ArticleStatus articleStatus, LocalDateTime publishedDataTime) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.articleAttachments = articleAttachments;
        this.articleReactions = articleReactions;
        this.topic = topic;
        this.discussions = discussions;
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.thumbnailUrl = thumbnailUrl;
        this.excerpt = excerpt;
        this.reactionCount = reactionCount;
        this.discussionCount = discussionCount;
        this.articleStatus = articleStatus;
        this.publishedDataTime = publishedDataTime;
    }

    public static Article createDraft(User user) {
        return Article.builder()
                .user(user)
                .title("")
                .content("")
                .excerpt("")
                .slug("")
                .thumbnailUrl("")
                .articleStatus(ArticleStatus.DRAFT)
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateTitle(String title) {
        if (isValidInput(title)) this.title = title;
    }

    public void updateContent(String content) {
        if (isValidInput(content)) this.content = content;
    }

    public void updateExcerpt(String excerpt) {
        if (isValidInput(excerpt)) this.excerpt = excerpt;
    }

    private boolean isValidInput(String input) {
        return input != null && !input.isEmpty() && !input.isBlank();
    }

    public void makePublish(LocalDateTime dateTime) {
        publishedDataTime = dateTime;
        articleStatus = ArticleStatus.PUBLIC;
        slug = generateSlug(title);
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
