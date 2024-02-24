package com.example.myrealog.api.controller.article;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleStatus;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.UserPrincipal;
import com.example.myrealog.v1.common.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.api.service.article.ArticleService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/api/v1/articles/draft")
    public ApiResponse<ArticleResponse> createDraft(@Authorized UserPrincipal principal) {
        return (ApiResponse.ok(articleService.createDraftOrGet(principal.getUserId())));
    }

    @GetMapping("/api/v1/articles/{username}/{slug}")
    public ApiResponse<ArticleResponse> getOneBySlugAndUsername(@PathVariable("username") String username,
                                                                @PathVariable("slug") String slug) {

        return ApiResponse.ok(articleService.findPublicArticleBySlugAndUsername(slug, username));
    }

    @GetMapping("/api/v1/articles/recent")
    public ApiResponse<List<ArticleResponse>> getRecentArticles() {
        final List<Article> recentArticles = articleService.getRecentArticles();
        final List<ArticleCardDto> dto = recentArticles.stream().map(ArticleCardDto::new).toList();
        return null;
    }

    // <------------------------ Refactored -------------------------------->

    @PostMapping("/api/v1/articles")
    public ResponseEntity<Void> publishArticle(@Authorized UserPrincipal principal,
                                               @RequestBody @Valid ArticlePublishFormRequest form) {

            final Article article = new Article(form.getTitle(), form.getContent(), form.getExcerpt(), ArticleStatus.PUBLIC);
            final Article publishedArticle = articleService.publishArticle(article, principal.getUserId());

            final String redirectUri = "/" + publishedArticle.getUser().getUsername() + "/" + publishedArticle.getSlug();
            return ResponseEntity.created(URI.create(redirectUri)).build();
    }





    @DeleteMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> deleteArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId) {

        articleService.deleteById(articleId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> updateArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId,
                                           @RequestBody @Valid ArticlePublishFormRequest form) {

        final Article updatedArticle = articleService.updateArticle(articleId, principal.getUserId(), form);
        final String redirectUri = "/" + updatedArticle.getUser().getUsername() + "/" + updatedArticle.getSlug();
        return ResponseEntity.created(URI.create(redirectUri)).build();
    }

    @Data
    static class ArticleCardDto {
        private Long articleId;
        private String title;
        private String slug;
        private String avatarUrl;
        private String displayName;
        private String username;
        private LocalDateTime createdDate;
        private String thumbnailUrl;
        private String excerpt;

        public ArticleCardDto(Article article) {
            this.articleId = article.getId();
            this.title = article.getTitle();
            this.slug = article.getSlug();
            this.avatarUrl = article.getUser().getProfile().getAvatarUrl();
            this.displayName = article.getUser().getProfile().getDisplayName();
            this.username = article.getUser().getUsername();
            this.createdDate = article.getCreatedDateTime();
            this.thumbnailUrl = article.getThumbnailUrl();
            this.excerpt = article.getExcerpt();
        }
    }

}
