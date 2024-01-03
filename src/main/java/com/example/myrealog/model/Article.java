package com.example.myrealog.model;

import com.example.myrealog.dto.request.CreateArticleRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Article {

    @Id @GeneratedValue
    @Column(name = "article_id")
    private Long id;
    private String title;
    private String content;
    private String slug;

    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    @Column(name = "meta_description")
    private String metaDescription;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;


    public static Article createFromRequest(CreateArticleRequest request) {
        return Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .slug(request.getSlug())
                .featuredImageUrl(request.getFeaturedImageUrl())
                .metaDescription(request.getMetaDescription())
                .status(request.getStatus())
                .build();
    }
}
