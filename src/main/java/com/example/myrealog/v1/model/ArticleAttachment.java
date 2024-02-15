package com.example.myrealog.v1.model;

import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.domain.article.Article;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "article_attachments")
public class ArticleAttachment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "article_attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private String url;
}
