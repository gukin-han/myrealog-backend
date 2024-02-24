package com.example.myrealog.api.service.article;

import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.article.ArticleStatus;
import com.example.myrealog.v1.common.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.v1.common.exception.NotEnoughDaysForPublishingException;
import com.example.myrealog.api.service.user.UserService;
import com.example.myrealog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ArticleService {

    public static final int SEVEN_DAYS = 7;
    public static final String ERROR_MESSAGE_NOT_EXISTING_ARTICLE = "존재하지 않는 아티클입니다.";
    private final ArticleRepository articleRepository;
    private final UserService userService;

    @Transactional
    public ArticleResponse createDraftOrGet(Long userId) {

        final User user = userService.findById(userId);
        final Optional<Article> optionalArticle = articleRepository.findTopByUserAndArticleStatusOrderByCreatedDateTimeDesc(user, ArticleStatus.DRAFT);
        if (optionalArticle.isEmpty()) {
            return createDraft(user);
        } else {
            return ArticleResponse.of(optionalArticle.get());
        }
    }

    @Transactional
    public ArticleResponse createDraft(User user) {
        final Article draft = Article.createDraft(user);
        final Article savedDraft = articleRepository.save(draft);
        return ArticleResponse.of(savedDraft);
    }

    @Transactional(readOnly = true)
    public ArticleResponse findPublicArticleBySlugAndUsername(final String slug,
                                                              final String username) {

        final Article article = articleRepository.findPublicArticleWithUserAndProfileBySlugAndUsername(slug, username)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTING_ARTICLE));
        return ArticleResponse.of(article);
    }

    @Transactional
    public Article publishArticle(Article article, Long userId) {
        final User findUser = userService.findById(userId);
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


    public List<Article> getRecentArticles() {
        return articleRepository.findAllWithUserProfile();
    }

    public Article findById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTING_ARTICLE));
    }
}
