package com.example.myrealog.api.service.article;

import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.article.ArticleStatus;
import com.example.myrealog.v1.common.dto.request.ArticlePublishFormRequest;
import com.example.myrealog.v1.common.exception.NotEnoughDaysForPublishingException;
import com.example.myrealog.v1.service.UserService;
import com.example.myrealog.v1.model.User;
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
    private final ArticleRepository articleRepository;
    private final UserService userService;

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


    public List<Article> getRecentArticles() {
        return articleRepository.findAllWithUserProfile();
    }

    @Transactional
    public ArticleResponse getDraftOrCreate(Long userId) {
        final User user = userService.findById(userId);
        final List<Article> drafts = articleRepository.findByUserAndArticleStatus(user, ArticleStatus.DRAFT);
        if (drafts.isEmpty()) {
            return createDraft(user);
        } else {
            return ArticleResponse.of(drafts.get(0));
        }
    }

    @Transactional
    public ArticleResponse createDraft(User user) {
        final Article draft = Article.createDraft(user);
        final Article savedDraft = articleRepository.save(draft);
        return ArticleResponse.of(savedDraft);
    }

    public Article findById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아티클입니다."));
    }
}
