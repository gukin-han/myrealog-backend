package com.example.myrealog.api.service.article;

import com.example.myrealog.api.service.article.ArticleService;
import com.example.myrealog.v1.model.User;
import com.example.myrealog.v1.repository.UserRepository;
import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.article.ArticleStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.myrealog.domain.article.ArticleStatus.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @AfterEach
    void tearDown() {
        articleRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("초안이 이미 있는 경우 초안을 반환한다.")
    @Test
    void getDraftIfExist(){
        //given
        final User user = User.builder()
                .email("email1@test.com")
                .username("username1")
                .build();
        final User savedUser = userRepository.save(user);
        final Article article1 = createArticle(savedUser, DRAFT);
        articleRepository.save(article1);

        //when
        final ArticleResponse draft = articleService.getDraftOrCreate(user.getId());

        //then
        assertThat(draft.getId()).isNotNull();
        assertThat(draft.getStatus()).isEqualTo(DRAFT);
    }

    @DisplayName("유저를 받아 초안을 생성한다.")
    @Test
    void createDraftWithUser(){
        //given
        final User user = User.builder()
                .email("email1@test.com")
                .username("username1")
                .build();
        final User savedUser = userRepository.save(user);

        //when
        final ArticleResponse draft = articleService.createDraft(savedUser);

        //then
        assertThat(draft.getId()).isNotNull();
        assertThat(draft.getStatus()).isEqualTo(DRAFT);
    }

    @DisplayName("초안이 없는 경우 초안을 생성해서 반환한다.")
    @Test
    void createDraftIfNotExist(){
        //given
        final User user = User.builder()
                .email("email1@test.com")
                .username("username1")
                .build();
        final User savedUser = userRepository.save(user);

        //when
        final ArticleResponse draft = articleService.getDraftOrCreate(savedUser.getId());

        //then
        assertThat(draft.getId()).isNotNull();
        assertThat(draft.getStatus()).isEqualTo(DRAFT);
    }

    @DisplayName("아티클 아이디를 받아 찾은 아티클을 반환한다.")
    @Test
    void findByIdTest(){
        //given
        final User user = User.builder()
                .email("email1@test.com")
                .username("username1")
                .build();
        final User savedUser = userRepository.save(user);
        final ArticleResponse draft = articleService.createDraft(savedUser);

        //when
        final Article findArticle = articleService.findById(draft.getId());

        //then
        assertThat(findArticle.getId()).isNotNull();
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