package com.example.myrealog.domain.discussion;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.v1.model.DiscussionReaction;
import com.example.myrealog.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "discussions")
public class Discussion extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "discussion_id")
    private Long id;

    private int depth;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_discussion_id")
    private Discussion parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "discussion", cascade = {REMOVE}, orphanRemoval = true)
    private List<DiscussionReaction> discussionReactions = new ArrayList<>();

    @Builder
    private Discussion(Long id, int depth, String content, Discussion parent, Article article, User user, List<DiscussionReaction> discussionReactions) {
        this.id = id;
        this.depth = depth;
        this.content = content;
        this.parent = parent;
        this.article = article;
        this.user = user;
        this.discussionReactions = discussionReactions;
    }

    public Discussion updateContent(String content) {
        this.content = content;
        return this;
    }
}
