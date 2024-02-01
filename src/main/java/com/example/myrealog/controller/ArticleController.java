package com.example.myrealog.controller;

import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import com.example.myrealog.service.ArticleService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.example.myrealog.model.Article.Status.PUBLIC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final OAuthService oAuthService;

    @PostMapping
    public ResponseEntity<Void> publishArticle(@Authorized User user,
                                               @RequestBody @Valid ArticlePublishFormRequest form) {

            final Article article = new Article(
                    user,
                    form.getTitle(),
                    form.getContent(),
                    form.getExcerpt(),
                    PUBLIC);

            final Article publishedArticle = articleService.publishArticle(article);

            String redirectUri = "/@" + user.getUsername() + "/" + publishedArticle.getSlug();
            return ResponseEntity
                    .created(URI.create(redirectUri))
                    .build();

    }

//    @GetMapping("/{username}/{slug}")
//    public ResponseEntity<Article> getOneByUsernameAndSlug(@PathVariable("username") String username,
//                                                           @PathVariable("slug") String slug) {
//
//    }

    @GetMapping("/drafts")
    public void getDrafts(HttpServletRequest request) {
        try {
            final String accessToken = request.getHeader("Authorization").substring(7);
            final String userId = oAuthService.validateTokenAndGetSubject(accessToken); // 유효기간 만료 및 위변조 에러 처리
            final List<Article> drafts = articleService.getDrafts(Long.parseLong(userId));

        } catch (JwtException e) {
            log.error("액세스 토큰 검증에 실패했습니다.");
        }
    }
}
