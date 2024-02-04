package com.example.myrealog.service;

import com.example.myrealog.exception.NotEnoughDaysForPublishingException;
import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.myrealog.model.Article.Status.PUBLIC;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Autowired
    EntityManager em;

    @DisplayName("최근 출판일이 7일 이후 인 경우 출판 성공")
    @Test
    void publishArticleTest() {

        // given
        User user = new User("email@test.com", "name");
        em.persist(user);
        Article article = new Article("title", "content", "excerpt", PUBLIC);

        // when
        articleService.publishArticle(article, user.getId());
        final User findUser = userService.findOneById(user.getId());

        // then
        assertThat(findUser.getRecentlyPublishedDate()).isNotNull();
        assertThat(findUser.getRecentlyPublishedDate()).isEqualTo(user.getRecentlyPublishedDate());
    }

    @DisplayName("최근 출판일이 7일 이내 인 경우 에러 발생")
    @Test
    void publishArticleTestWithin7Days() {

        // given
        User user = new User("email@test.com", "name");
        em.persist(user);
        Article article1 = new Article("title", "content", "excerpt", PUBLIC);
        Article article2 = new Article("title", "content", "excerpt", PUBLIC);
        articleService.publishArticle(article1, user.getId());

        // when/then
        assertThatThrownBy(() -> articleService.publishArticle(article2, user.getId()))
                .isInstanceOf(NotEnoughDaysForPublishingException.class);
    }


}