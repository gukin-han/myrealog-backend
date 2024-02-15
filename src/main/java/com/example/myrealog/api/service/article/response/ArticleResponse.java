package com.example.myrealog.api.service.article.response;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String slug;
    private String thumbnailUrl;
    private String excerpt;
    private ArticleStatus status;

    @Builder
    private ArticleResponse(Long id,
                            String title,
                            String content,
                            String slug,
                            String thumbnailUrl,
                            String excerpt,
                            ArticleStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.thumbnailUrl = thumbnailUrl;
        this.excerpt = excerpt;
        this.status = status;
    }

    public static ArticleResponse of(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .slug(article.getSlug())
                .thumbnailUrl(article.getThumbnailUrl())
                .excerpt(article.getExcerpt())
                .status(article.getArticleStatus())
                .build();
    }
}
