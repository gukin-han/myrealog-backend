package com.example.myrealog.api.service.article;

import com.example.myrealog.api.controller.article.request.ArticleUpdateRequest;
import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.article.ArticleStatus;
import com.example.myrealog.api.service.user.UserService;
import com.example.myrealog.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ArticleService {

    public static final Long SEVEN_DAYS = 7L;
    public static final String ERROR_MESSAGE_NOT_EXISTING_ARTICLE = "존재하지 않는 아티클입니다.";
    public static final String ERROR_MESSAGE_NOT_ENOUGH_DAYS_TO_PUBLISH = "기간이 충분하지 않아 게시할 수 없습니다.";
    public static final String ERROR_MESSAGE_ALREADY_PUBLIC_ARTICLE = "이미 게시된 아티클입니다.";
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
    public ArticleResponse publishArticle(Long userId,
                                          Long articleId,
                                          LocalDateTime now) {

        final User findUser = userService.findById(userId);
        final LocalDateTime mostRecentlyPublishedDateTime = findUser.getRecentlyPublishedDateTime();
        final LocalDateTime publishableDateTimeLimit = mostRecentlyPublishedDateTime.plusDays(SEVEN_DAYS);
        final Article article = findUpdatableArticleByArticleIdAndUserId(articleId, userId);
        validatePublishable(now, publishableDateTimeLimit, article);
        article.makePublish(now);
        findUser.updateRecentlyPublishedDateTime(now);
        return ArticleResponse.of(article);
    }

    private void validatePublishable(LocalDateTime now, LocalDateTime publishableDateTimeLimit, Article article) {
        if (cannotPublish(now, publishableDateTimeLimit)) {
            throw new IllegalStateException(ERROR_MESSAGE_NOT_ENOUGH_DAYS_TO_PUBLISH);
        }

        if (article.getArticleStatus() != ArticleStatus.DRAFT) {
            throw new IllegalStateException(ERROR_MESSAGE_ALREADY_PUBLIC_ARTICLE);
        }
    }

    private boolean cannotPublish(LocalDateTime now, LocalDateTime publishableDateTimeLimit) {
        return !now.isAfter(publishableDateTimeLimit);
    }

    @Transactional
    public void deleteById(Long articleId, Long userId) {
        final Article findArticle = findUpdatableArticleByArticleIdAndUserId(articleId, userId);
        articleRepository.delete(findArticle);
    }

    @Transactional
    public Article updateArticle(Long articleId, Long userId, ArticleUpdateRequest request) {
        final Article findArticle = findUpdatableArticleByArticleIdAndUserId(articleId, userId);

        findArticle.updateTitle(request.getTitle());
        findArticle.updateContent(request.getContent());
        findArticle.updateExcerpt(request.getExcerpt());

        return findArticle;
    }

    @Transactional
    public Article findUpdatableArticleByArticleIdAndUserId(Long articleId, Long userId) {
        return articleRepository.findByArticleIdAndUserId(articleId, userId)
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<ArticleResponse> getAllRecentArticlesOrderByCreatedDate() {
        return articleRepository
                .findAllWithUserAndProfile()
                .stream()
                .map(ArticleResponse::ofMetaData)
                .toList();
    }

    public Article findById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTING_ARTICLE));
    }
}
