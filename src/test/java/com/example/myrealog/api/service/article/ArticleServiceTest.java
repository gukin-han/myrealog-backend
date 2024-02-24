package com.example.myrealog.api.service.article;

import com.example.myrealog.domain.user.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.article.ArticleStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.myrealog.domain.article.ArticleStatus.DRAFT;
import static com.example.myrealog.domain.article.ArticleStatus.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        final User user = createUser("username1");
        final User savedUser = userRepository.save(user);
        final Article article1 = createArticle(savedUser, DRAFT);
        articleRepository.save(article1);

        //when
        final ArticleResponse draft = articleService.createDraftOrGet(user.getId());

        //then
        assertThat(draft.getId()).isNotNull();
        assertThat(draft.getStatus()).isEqualTo(DRAFT);
    }

    @DisplayName("유저를 받아 초안을 생성한다.")
    @Test
    void createDraftWithUser(){
        //given
        final User user = createUser("username1");
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
        final User user = createUser("username1");
        final User savedUser = userRepository.save(user);

        //when
        final ArticleResponse draft = articleService.createDraftOrGet(savedUser.getId());

        //then
        assertThat(draft.getId()).isNotNull();
        assertThat(draft.getStatus()).isEqualTo(DRAFT);
    }

    @DisplayName("아티클 아이디를 받아 찾은 아티클을 반환한다.")
    @Test
    void findByIdTest(){
        //given
        final User user = createUser("username1");
        final User savedUser = userRepository.save(user);
        final ArticleResponse draft = articleService.createDraft(savedUser);

        //when
        final Article findArticle = articleService.findById(draft.getId());

        //then
        assertThat(findArticle.getId()).isNotNull();
    }

    @DisplayName("아티클 슬러그와 유저이름을 받아 공개된 아티클을 찾아 반환한다.")
    @Test
    void findArticleBySlugAndUsernameTest(){
        //given
        final String targetUsername = "usernameTest";
        final String targetSlug = "slugTest";

        final User user = createUser(targetUsername);
        final User savedUser = userRepository.save(user);
        final Article article = createArticle(user, targetSlug, PUBLIC);
        final Article savedArticle = articleRepository.save(article);

        //when
        final ArticleResponse findArticle = articleService.findPublicArticleBySlugAndUsername(targetSlug, targetUsername);

        //then
        assertThat(findArticle.getSlug()).isEqualTo(targetSlug);
        assertThat(findArticle.getUser().getUsername()).isEqualTo(targetUsername);
    }

    @DisplayName("아티클 슬러그와 유저이름을 받아 공개된 아티클을 찾을때 없는 경우 에러를 던진다.")
    @Test
    void findArticleBySlugAndUsernameNotExisting_thenThrowError(){
        //given
        final String targetUsername = "usernameTest";
        final String targetSlug = "slugTest";

        //when
        //then
        assertThatThrownBy(() -> articleService.findPublicArticleBySlugAndUsername(targetSlug, targetUsername))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아티클입니다.");
    }

    private Article createArticle(User user, String slug, ArticleStatus articleStatus) {
        return Article.builder().
                user(user)
                .content("content")
                .title("title")
                .articleStatus(articleStatus)
                .slug(slug)
                .build();
    }

    private User createUser(String username) {
        return User.builder()
                .email("email1@test.com")
                .username(username)
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