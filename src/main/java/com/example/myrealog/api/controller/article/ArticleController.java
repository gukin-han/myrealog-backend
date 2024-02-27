package com.example.myrealog.api.controller.article;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.controller.article.request.ArticleUpdateRequest;
import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.UserPrincipal;
import com.example.myrealog.api.service.article.ArticleService;
import jakarta.validation.Valid;
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
        return ApiResponse.ok(articleService.createDraftOrGet(principal.getUserId()));
    }

    @GetMapping("/api/v1/articles/{username}/{slug}")
    public ApiResponse<ArticleResponse> getOneBySlugAndUsername(@PathVariable("username") String username,
                                                                @PathVariable("slug") String slug) {

        return ApiResponse.ok(articleService.findPublicArticleBySlugAndUsername(slug, username));
    }

    @GetMapping("/api/v1/articles/recent")
    public ApiResponse<List<ArticleResponse>> getAllRecentArticlesOrderByCreatedDate() {
        return ApiResponse.ok(articleService.getAllRecentArticlesOrderByCreatedDate());
    }

    @PatchMapping("/api/v1/articles/{articleId}/publish")
    public ApiResponse<ArticleResponse> publishArticle(@Authorized UserPrincipal principal,
                                                       @PathVariable("articleId") Long articleId) {

        return ApiResponse.ok(
                articleService.publishArticle(
                        principal.getUserId(),
                        articleId,
                        LocalDateTime.now()
                )
        );
    }

    // <------------------------ Refactored -------------------------------->

    @DeleteMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> deleteArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId) {

        articleService.deleteById(articleId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> updateArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId,
                                           @RequestBody @Valid ArticleUpdateRequest form) {

        final Article updatedArticle = articleService.updateArticle(articleId, principal.getUserId(), form);
        final String redirectUri = "/" + updatedArticle.getUser().getUsername() + "/" + updatedArticle.getSlug();
        return ResponseEntity.created(URI.create(redirectUri)).build();
    }
}
