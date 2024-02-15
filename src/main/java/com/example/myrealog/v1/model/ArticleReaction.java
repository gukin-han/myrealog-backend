package com.example.myrealog.v1.model;

import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "article_reactions", uniqueConstraints = {@UniqueConstraint(columnNames = {"article_id", "user_id"})})
public class ArticleReaction extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "article_reaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
