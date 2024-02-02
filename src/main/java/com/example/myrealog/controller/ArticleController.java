package com.example.myrealog.controller;

import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.UserPrincipal;
import com.example.myrealog.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.model.Article;
import com.example.myrealog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

            final String redirectUri = "/@" + publishedArticle.getUser().getUsername() + "/" + publishedArticle.getSlug();
            return ResponseEntity
                    .created(URI.create(redirectUri))
                    .build();
    }

//    @GetMapping("/{username}/{slug}")
//    public ResponseEntity<Article> getOneByUsernameAndSlug(@PathVariable("username") String username,
//                                                           @PathVariable("slug") String slug) {
//
//    }

}
