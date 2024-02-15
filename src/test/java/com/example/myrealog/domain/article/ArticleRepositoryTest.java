package com.example.myrealog.domain.article;

import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.myrealog.domain.article.ArticleStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저와 아티클 상태를 가지고 아티클들을 조회한다.")
    @Test
    void findByUserAndArticleStatusTest(){
        //given

        final User user = User.builder()
                .email("email1@test.com")
                .username("username1")
                .build();
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, DRAFT);
        final Article article2 = createArticle(savedUser, PUBLIC);
        final Article article3 = createArticle(savedUser, DRAFT);
        articleRepository.saveAll(List.of(article1, article2, article3));

        //when
        final List<Article> findArticles = articleRepository.findByUserAndArticleStatus(savedUser, DRAFT);

        //then
        assertThat(findArticles).hasSize(2)
                .extracting("articleStatus")
                .containsExactlyInAnyOrder(DRAFT, DRAFT);
    }

    private static Article createArticle(User savedUser, ArticleStatus articleStatus) {
        return Article.builder()
                .user(savedUser)
                .content("content")
                .title("title")
                .articleStatus(articleStatus)
                .build();
    }


}