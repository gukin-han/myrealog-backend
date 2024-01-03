package com.example.myrealog.controller;

import com.example.myrealog.dto.request.CreateArticleRequest;
import com.example.myrealog.model.Article;
import com.example.myrealog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/articles")
    public List<Article> getArticles() {
        return articleService.getArticles();
    }

    @PostMapping("/articles")
    public Long createArticle(@RequestBody CreateArticleRequest request) {
        return articleService.createArticle(request);
    }
}
