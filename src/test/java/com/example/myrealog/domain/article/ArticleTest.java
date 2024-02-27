package com.example.myrealog.domain.article;

import com.example.myrealog.domain.user.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.myrealog.domain.article.ArticleStatus.DRAFT;
import static com.example.myrealog.domain.article.ArticleStatus.PUBLIC;

@Transactional
@SpringBootTest
class ArticleTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @DisplayName("아티클을 공개 상태로 게시한다.")
    @Test
    void makeArticlePublish(){
        //given
        final LocalDateTime givenDateTime = LocalDateTime.now();
        final User user = createUser();
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, DRAFT);
        final Article savedArticle1 = articleRepository.save(article1);

        //when
        article1.makePublish(givenDateTime);

        //then
        Assertions.assertThat(article1.getArticleStatus()).isEqualTo(PUBLIC);
        Assertions.assertThat(article1.getSlug()).isNotBlank();
        Assertions.assertThat(article1.getPublishedDataTime()).isEqualTo(givenDateTime);
    }

    private User createUser() {
        return User.builder()
                .email("email1@test.com")
                .username("username")
                .profile(Profile.builder().build())
                .build();
    }

    private Article createArticle(User savedUser, ArticleStatus articleStatus) {
        return Article.builder()
                .user(savedUser)
                .content("content")
                .title("title")
                .articleStatus(articleStatus)
                .build();
    }



}