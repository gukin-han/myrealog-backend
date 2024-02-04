package com.example.myrealog.repository;

import com.example.myrealog.model.Article;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.service.ArticleService;
import com.example.myrealog.service.UserService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.myrealog.model.Article.Status.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findBySlugAndUsernameTest() {
        //given
        final Profile profile = new Profile("displayNameTest", "bioTest");
        final User user1 = new User("test1@test.com", "test1");
        final User signedUpUser = userService.signUp(user1, profile);

        final Article article1 = new Article("title1", "content1", "excerpt1", PUBLIC);
        final Article publishedArticle1 = articleService.publishArticle(article1, signedUpUser.getId());

        em.flush();
        em.clear();
        //when

        final Optional<Article> findArticle1 = articleRepository.findBySlugAndUsername(publishedArticle1.getSlug(), user1.getUsername());
        //then

        assertThat(findArticle1.isPresent()).isTrue();
        assertThat(findArticle1.get().getTitle()).isEqualTo("title1");
        assertThat(findArticle1.get().getUser().getUsername()).isEqualTo("test1");
    }
}