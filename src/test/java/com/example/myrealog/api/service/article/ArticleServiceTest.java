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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        final User user = createUser("username1", "email1@test.com", LocalDateTime.now());
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
        final User user = createUser("username1", "email1@test.com", LocalDateTime.now());
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
        final User user = createUser("username1", "email1@test.com", LocalDateTime.now());
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
        final User user = createUser("username1", "email1@test.com", LocalDateTime.now());
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

        final User user = createUser(targetUsername, "email1@test.com", LocalDateTime.now());
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

    @DisplayName("생성일을 기준으로 최근 아티클을 정렬하여 가져온다.")
    @Test
    void getAllRecentArticlesOrderByCreatedDateTest(){
        //given
        final User user1 = createUser("username1", "email1@test.com", LocalDateTime.now());
        final User savedUser1 = userRepository.save(user1);

        final User user2 = createUser("username2", "email2@test.com", LocalDateTime.now());
        final User savedUser2 = userRepository.save(user2);

        final Article article1 = createArticle(user1, "slug", PUBLIC);
        final Article article2 = createArticle(user2, "slug", PUBLIC);
        final Article article3 = createArticle(user1, "slug", PUBLIC);
        final Article article4 = createArticle(user2, "slug", PUBLIC);
        final Article article5 = createArticle(user1, "slug", DRAFT);
        final List<Article> savedArticles = articleRepository.saveAll(List.of(article1, article2, article3, article4, article5));
        final Article mostRecentOne = savedArticles.get(savedArticles.size() - 2);

        //when
        final List<ArticleResponse> findArticles = articleService.getAllRecentArticlesOrderByCreatedDate();


        //then
        assertThat(findArticles).hasSize(4);
        assertThat(findArticles.get(0).getId()).isEqualTo(mostRecentOne.getId());
    }

    @DisplayName("아티클을 공개로 게시한다.")
    @Test
    void makeDraftPublicTest(){
        //given
        final LocalDateTime recentlyPublishedDateTime = LocalDateTime.of(2023, 2, 19, 23, 59);
        final LocalDateTime currentDateTime = LocalDateTime.of(2023, 2, 27, 0, 0);

        final User user1 = createUser("username1", "email1@test.com", recentlyPublishedDateTime);
        final User savedUser1 = userRepository.save(user1);

        final Article article1 = createArticle(user1, "slug", DRAFT);
        final Article savedArticle1 = articleRepository.save(article1);

        //when
        articleService.publishArticle(user1.getId(), savedArticle1.getId(), currentDateTime);

        //then
        final Optional<Article> optionalArticle = articleRepository.findById(savedArticle1.getId());
        final Optional<User> optionalUser = userRepository.findById(savedUser1.getId());
        final Article updatedArticle = optionalArticle.get();
        assertThat(optionalUser.get().getRecentlyPublishedDateTime()).isEqualTo(currentDateTime);
        assertThat(updatedArticle.getArticleStatus()).isEqualTo(PUBLIC);
        assertThat(updatedArticle.getPublishedDataTime()).isEqualTo(currentDateTime);

    }

    @DisplayName("아티클을 게시할때 최근 게시일이 7일 이내이면 에러를 던진다.")
    @Test
    void makeDraftPublicWithRecentPublishDateWithinSevenDays_thenThrowError(){
        //given
        final LocalDateTime recentlyPublishedDateTime = LocalDateTime.of(2023, 2, 20, 0, 0);
        final LocalDateTime currentDateTime = LocalDateTime.of(2023, 2, 27, 0, 0);

        final User user1 = createUser("username1", "email1@test.com", recentlyPublishedDateTime);
        final User savedUser1 = userRepository.save(user1);

        final Article article1 = createArticle(user1, "slug", DRAFT);
        final Article savedArticle1 = articleRepository.save(article1);

        //when
        //then
        assertThatThrownBy(() -> articleService.publishArticle(user1.getId(), savedArticle1.getId(), currentDateTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ArticleService.ERROR_MESSAGE_NOT_ENOUGH_DAYS_TO_PUBLISH);
    }

    @DisplayName("아티클을 게시할때 주어진 아티클이 DRAFT 상태가 아닌 경우 에러를 던진다.")
    @Test
    void makeDraftPublicWithoutArticleStatusPublic_thenThrowError(){
        //given
        final LocalDateTime recentlyPublishedDateTime = LocalDateTime.of(2023, 2, 19, 0, 0);
        final LocalDateTime currentDateTime = LocalDateTime.of(2023, 2, 27, 0, 0);

        final User user1 = createUser("username1", "email1@test.com", recentlyPublishedDateTime);
        final User savedUser1 = userRepository.save(user1);

        final Article article1 = createArticle(user1, "slug", PUBLIC);
        final Article savedArticle1 = articleRepository.save(article1);

        //when
        //then
        assertThatThrownBy(() -> articleService.publishArticle(user1.getId(), savedArticle1.getId(), currentDateTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ArticleService.ERROR_MESSAGE_ALREADY_PUBLIC_ARTICLE);
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

    private User createUser(String username, String email, LocalDateTime dateTime) {
        return User.builder()
                .email(email)
                .recentlyPublishedDateTime(dateTime)
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