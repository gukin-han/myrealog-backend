package com.example.myrealog.service;

import com.example.myrealog.dto.request.CreateArticleRequest;
import com.example.myrealog.model.Article;
import com.example.myrealog.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    public Long createArticle(CreateArticleRequest request) {
        Article article = new Article().createFromRequest(request);
        articleRepository.save(article);
        return article.getId();
    }
}
