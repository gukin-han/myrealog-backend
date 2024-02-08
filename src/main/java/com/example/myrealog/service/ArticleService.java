package com.example.myrealog.service;

import com.example.myrealog.common.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.common.exception.NotEnoughDaysForPublishingException;
import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    public static final int SEVEN_DAYS = 7;
    private final ArticleRepository articleRepository;
    private final UserService userService;

    @Transactional
    public Article publishArticle(Article article, Long userId) {
        final User findUser = userService.findOneById(userId);
        final LocalDateTime date = findUser.getRecentlyPublishedDate();

        if (true) {
            findUser.updateRecentlyPublishedDate(LocalDateTime.now());
            article.setUser(findUser);
            return articleRepository.save(article);
        } else {
            throw new NotEnoughDaysForPublishingException();
        }
    }

    private boolean canPublishArticle(LocalDateTime date) {
        return date == null || isMoreThanSevenDays(date);
    }

    private boolean isMoreThanSevenDays(LocalDateTime date) {
        return Duration
                .between(date, LocalDateTime.now())
                .toDays() >= SEVEN_DAYS;
    }

    @Transactional(readOnly = true)
    public Article findArticleBySlugAndUsername(final String slug,
                                                final String username) {

        return articleRepository.findBySlugAndUsername(slug, username)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void deleteById(Long articleId, Long userId) {
        final Article findArticle = findUpdatableArticleByArticleIdAndUserId(articleId, userId);
        articleRepository.delete(findArticle);
    }

    @Transactional
    public Article updateArticle(Long articleId, Long userId, ArticlePublishFormRequest form) {
        final Article findArticle = findUpdatableArticleByArticleIdAndUserId(articleId, userId);

        findArticle.updateTitle(form.getTitle());
        findArticle.updateContent(form.getContent());
        findArticle.updateExcerpt(form.getExcerpt());

        return findArticle;
    }

    @Transactional
    public Article findUpdatableArticleByArticleIdAndUserId(Long articleId, Long userId) {
        return articleRepository.findByArticleIdAndUserId(articleId, userId)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional(readOnly = true)
    public List<Article> getRecentArticles() {
        return articleRepository.findAllWithUserProfile();
    }
}
