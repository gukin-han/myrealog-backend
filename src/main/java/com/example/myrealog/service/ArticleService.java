package com.example.myrealog.service;

import com.example.myrealog.model.Article;
import com.example.myrealog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.myrealog.model.Article.Status.DRAFT;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article publishArticle(Article article) {
        return articleRepository.save(article);
    }

    public List<Article> getDrafts(Long userId) {
        return articleRepository.findByUserIdAndStatus(userId, DRAFT);
    }
}
