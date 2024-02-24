package com.example.myrealog.domain.discussion;

import com.example.myrealog.api.service.discussion.DiscussionService;
import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.user.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class DiscussionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private DiscussionService discussionService;

    @DisplayName("아티클 아이디로 모든 디스커션을 조회한다.")
    @Test
    void findAllByArticleIdTest(){
        //given

        final User user1 = createUserWithProfile("email1@test.com", "username1");
        final User savedUser1 = userRepository.save(user1);

        final User user2 = createUserWithProfile("email2@test.com", "username2");
        final User savedUser2 = userRepository.save(user2);

        final Article article = createArticle(user1);
        final Article savedArticle = articleRepository.save(article);

        final DiscussionCreateServiceRequest request = DiscussionCreateServiceRequest.builder().content("test content").build();
        final Long articleId = savedArticle.getId();
        final DiscussionResponse discussion1 = discussionService.createDiscussion(savedUser1.getId(), articleId, request);
        final DiscussionResponse discussion2 = discussionService.createDiscussion(savedUser2.getId(), articleId, request);

        //when
        final List<Discussion> discussions = discussionRepository.findAllByArticleId(articleId);

        //then
        assertThat(discussions).hasSize(2)
                .extracting("content", "user.id", "user.profile.id")
                .contains(
                        Tuple.tuple("test content", discussion1.getUser().getId(), discussion1.getUser().getProfile().getId()),
                        Tuple.tuple("test content", discussion2.getUser().getId(), discussion2.getUser().getProfile().getId())
                );
    }

    @DisplayName("디스커션 아이디로 디스커션와 함께 유저를 조회한다.")
    @Test
    void findDiscussionWithProfileByIdTest(){
        //given
        final String email = "email1@test.com";
        final String username = "username1";
        final User user = createUserWithProfile(email, username);
        final User savedUser = userRepository.save(user);

        final Article article = createArticle(savedUser);
        final Article savedArticle = articleRepository.save(article);

        final Discussion discussion = createDiscussion(user, article, null);
        final Discussion savedDiscussion = discussionRepository.save(discussion);
        final Long discussionId = savedDiscussion.getId();

        //when
        final Optional<Discussion> optionalDiscussion = discussionRepository.findDiscussionWithUserById(discussionId);

        //then
        assertThat(optionalDiscussion.isPresent()).isTrue();
        final Discussion findDiscussion = optionalDiscussion.get();
        assertThat(findDiscussion.getId()).isNotNull();

        final User findUser = findDiscussion.getUser();
        assertThat(findUser.getId()).isNotNull();
        assertThat(findUser.getEmail()).isEqualTo(email);
        assertThat(findUser.getUsername()).isEqualTo(username);
    }

    @DisplayName("존재하지 않는 디스커션 아이디로 조회하면 빈 옵셔널을 반환한다.")
    @Test
    void findDiscussionByNotExistingDiscussionId_ThenReturnEmptyOptional(){
        //given
        final Long NotExistingDiscussionId = 1L;

        //when
        final Optional<Discussion> optionalDiscussion = discussionRepository.findDiscussionWithUserById(NotExistingDiscussionId);

        //then
        assertThat(optionalDiscussion.isEmpty()).isTrue();
    }

    private static User createUserWithProfile(String email, String username) {
        final Profile profile = Profile.builder()
                .displayName("displayName")
                .build();

        return User.builder()
                .email(email)
                .username(username)
                .profile(profile)
                .build();
    }

    private static Article createArticle(User user) {
        return Article.builder()
                .title("title1")
                .content("content1")
                .excerpt("excerpt1")
                .user(user)
                .build();
    }

    private Discussion createDiscussion(User user, Article article, Discussion parent) {
        return Discussion.builder()
                .content("test content")
                .user(user)
                .article(article)
                .parent(parent)
                .build();
    }
}