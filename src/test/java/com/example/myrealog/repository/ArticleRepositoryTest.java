package com.example.myrealog.repository;

import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.myrealog.model.Article.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("특정 유저의 타입이 DRAFT인 아티클들을 반환한다")
    void getDraftsTest() {
        final User user1 = new User("email1", "name1");
        final User user2 = new User("email2", "name2");
        em.persist(user1);
        em.persist(user2);

        final Article article1 = new Article(user1, "title1", "content1", "excerpt1", DRAFT);
        final Article article2 = new Article(user2, "title2", "content2", "excerpt2", DRAFT);
        final Article article3 = new Article(user1, "title3", "content3", "excerpt3", PUBLIC);
        final Article article4 = new Article(user2, "title4", "content4", "excerpt4", PUBLIC);
        final Article article5 = new Article(user1, "title5", "content5", "excerpt5", PRIVATE);
        final Article article6 = new Article(user2, "title6", "content6", "excerpt6", PRIVATE);
        em.persist(article1);
        em.persist(article2);
        em.persist(article3);
        em.persist(article4);
        em.persist(article5);
        em.persist(article6);

        final List<Article> findDrafts = articleRepository.findByUserIdAndStatus(user1.getId(), DRAFT);
        assertThat(findDrafts.size()).isEqualTo(1);
        assertThat(findDrafts.get(0).getTitle()).isEqualTo("title1");
    }
}