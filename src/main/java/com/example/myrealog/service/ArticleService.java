package com.example.myrealog.service;

import com.example.myrealog.exception.NotEnoughDaysForPublishingException;
import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.myrealog.model.Article.Status.DRAFT;

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

        if (canPublishArticle(date)) {
            findUser.updateRecentlyPublishedDate(LocalDateTime.now());
            article.setAuthor(findUser);
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
    public List<Article> getDrafts(Long userId) {
        return articleRepository.findByUserIdAndStatus(userId, DRAFT);
    }
}
