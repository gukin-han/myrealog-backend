package com.example.myrealog.domain.article;

import com.example.myrealog.domain.user.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.myrealog.domain.article.ArticleStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class
ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저와 아티클 상태를 가지고 가장 최근 아티클을 찾는다.")
    @Test
    void findByUserAndArticleStatusTest(){
        //given
        final User user = createUser("username1");
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, DRAFT, "slugTest");
        final Article savedArticle1 = articleRepository.save(article1);
        final Article article2 = createArticle(savedUser, PUBLIC, "slugTest");
        final Article savedArticle2 = articleRepository.save(article2);
        final Article article3 = createArticle(savedUser, DRAFT, "slugTest");
        final Article recentDraft = articleRepository.save(article3);

        //when
        final Optional<Article> optionalArticle = articleRepository.findTopByUserAndArticleStatusOrderByCreatedDateTimeDesc(savedUser, DRAFT);

        //then
        assertThat(optionalArticle.isPresent()).isTrue();
        assertThat(optionalArticle.get().getId()).isEqualTo(recentDraft.getId());
    }



    @DisplayName("작성된 아티클이 없는 경우 유저와 아티클 상태를 가지고 가장 최근 아티클을 찾을때 빈 엔티티를 반환한다.")
    @Test
    void findByUserAndArticleStatusWithoutWrittenArticle_thenThrowError(){
        //given
        final User user = createUser("username1");
        final User savedUser = userRepository.save(user);

        //when
        final Optional<Article> optionalArticle = articleRepository.findTopByUserAndArticleStatusOrderByCreatedDateTimeDesc(savedUser, DRAFT);

        //then
        assertThat(optionalArticle.isEmpty()).isTrue();
    }

    @DisplayName("슬러그와 유저이름으로 작성자 엔티티를 포함한 공개된 아티클을 찾아 포함하여 반환한다.")
    @Test
    void findArticleWithUserAndProfileBySlugAndUsernameTest(){
        //given
        final String targetUsername = "username1";
        final String targetSlug = "slugTest1";

        final User user = createUser(targetUsername);
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, PUBLIC, targetSlug);
        final Article savedArticle1 = articleRepository.save(article1);
        final Article article2 = createArticle(savedUser, DRAFT, "slugTest2");
        final Article savedArticle2 = articleRepository.save(article2);

        //when
        final Optional<Article> optionalArticle = articleRepository.findPublicArticleWithUserAndProfileBySlugAndUsername(targetSlug, targetUsername);

        //then
        assertThat(optionalArticle.isPresent()).isTrue();
        final Article findArticle = optionalArticle.get();
        final User findArticleUser = findArticle.getUser();
        assertThat(findArticle.getSlug()).isEqualTo(targetSlug);
        assertThat(findArticleUser.getUsername()).isEqualTo(targetUsername);
        assertThat(findArticleUser.getProfile().getId()).isNotNull();
    }

    @DisplayName("슬러그와 유저이름으로 공개된 아티클을 찾을때 초안은 반환하지 않는다.")
    @Test
    void findArticleWithoutAnyExistingPublicArticle_thenReturnEmptyEntity(){
        //given
        final String targetUsername = "username1";
        final String targetSlug = "slugTest1";

        final User user = createUser(targetUsername);
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, DRAFT, targetSlug);
        final Article savedArticle1 = articleRepository.save(article1);

        //when
        final Optional<Article> optionalArticle = articleRepository
                .findPublicArticleWithUserAndProfileBySlugAndUsername(targetSlug, targetUsername);

        //then
        assertThat(optionalArticle.isEmpty()).isTrue();
    }

    @DisplayName("아티클의 메타정보를 최신 순서대로 모두 조회한다.")
    @Test
    void test(){
        //given
        final User user = createUser("username");
        final User savedUser = userRepository.save(user);

        final Article article1 = createArticle(savedUser, PUBLIC, "slug");
        final Article article2 = createArticle(savedUser, PUBLIC, "slug");
        final Article article3 = createArticle(savedUser, PUBLIC, "slug");
        final Article article4 = createArticle(savedUser, DRAFT, "slug");
        final List<Article> savedArticles = articleRepository.saveAll(List.of(article1, article2, article3, article4));

        //when
        final List<Article> findArticles = articleRepository.findAllWithUserAndProfile();
        final Article mostRecentOne = savedArticles.get(2);

        //then
        assertThat(findArticles).hasSize(3);
        assertThat(findArticles.get(0).getId()).isEqualTo(mostRecentOne.getId());
        assertThat(findArticles.get(0).getUser().getId()).isNotNull();
        assertThat(findArticles.get(0).getUser().getProfile().getId()).isNotNull();
    }

    private static Article createArticle(User savedUser, ArticleStatus articleStatus, String slug) {
        return Article.builder()
                .user(savedUser)
                .content("content")
                .title("title")
                .slug(slug)
                .articleStatus(articleStatus)
                .build();
    }

    private static User createUser(String username) {
        return User.builder()
                .email("email1@test.com")
                .username(username)
                .profile(Profile.builder().build())
                .build();
    }
}