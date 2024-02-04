package com.example.myrealog.controller;

import com.example.myrealog.common.auth.Authorized;
import com.example.myrealog.common.auth.UserPrincipal;
import com.example.myrealog.common.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.common.dto.response.ResponseWrapper;
import com.example.myrealog.model.Article;
import com.example.myrealog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.myrealog.model.Article.Status.PUBLIC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<Void> publishArticle(@Authorized UserPrincipal principal,
                                               @RequestBody @Valid ArticlePublishFormRequest form) {

            final Article article = new Article(form.getTitle(), form.getContent(), form.getExcerpt(), PUBLIC);
            final Article publishedArticle = articleService.publishArticle(article, principal.getUserId());

            final String redirectUri = "/" + publishedArticle.getUser().getUsername() + "/" + publishedArticle.getSlug();
            return ResponseEntity.created(URI.create(redirectUri)).build();
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentArticles() {
        final List<Article> recentArticles = articleService.getRecentArticles();
        final List<ArticleCardDto> dto = recentArticles.stream().map(ArticleCardDto::new).toList();
        return ResponseEntity.ok(ResponseWrapper.of(dto));
    }

    @GetMapping("/{username}/{slug}")
    public ResponseEntity<?> getOneBySlugAndUsername(@PathVariable("username") String username,
                                                     @PathVariable("slug") String slug) {

        final Article article = articleService.findArticleBySlugAndUsername(slug, username);
        return ResponseEntity.ok(ResponseWrapper.of(new ArticleViewDto(article)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId) {

        articleService.deleteById(articleId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@Authorized UserPrincipal principal,
                                           @PathVariable("id") Long articleId,
                                           @RequestBody @Valid ArticlePublishFormRequest form) {

        articleService.updateArticle(articleId,principal.getUserId(), form);
        return ResponseEntity.ok().build();
    }

    @Data
    static class ArticleViewDto {
        private Long articleId;
        private String title;
        private LocalDateTime createdDate;
        private String displayName;
        private String avatarUrl;
        private String content;
        private String excerpt;

        public ArticleViewDto(Article article) {
            this.articleId = getArticleId();
            this.title = article.getTitle();
            this.createdDate = article.getCreatedDate();
            this.displayName = article.getUser().getProfile().getDisplayName();
            this.avatarUrl = article.getUser().getProfile().getAvatarUrl();
            this.content = article.getContent();
            this.excerpt = article.getExcerpt();
        }
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
            this.createdDate = article.getCreatedDate();
            this.thumbnailUrl = article.getThumbnailUrl();
            this.excerpt = article.getExcerpt();
        }
    }

}
