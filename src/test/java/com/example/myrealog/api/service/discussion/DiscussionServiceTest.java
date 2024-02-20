package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.request.DiscussionUpdateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.DiscussionRepository;
import com.example.myrealog.domain.profile.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import com.example.myrealog.v1.common.exception.DiscussionNotFoundException;
import com.example.myrealog.v1.common.exception.UnauthorizedException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class DiscussionServiceTest {

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @DisplayName("디스커션을 생성한다.")
    @Test
    void createDiscussionTest(){
        //given
        final User user = createUserWithProfile("eamil1@test.com", "username1");
        final User savedUser = userRepository.save(user);

        final Article article = createArticle(savedUser);
        final Article savedArticle = articleRepository.save(article);

        final DiscussionCreateServiceRequest request = DiscussionCreateServiceRequest.builder().build();

        //when
        final DiscussionResponse discussion = discussionService.createDiscussion(savedUser.getId(), savedArticle.getId(), request);

        //then
        assertThat(discussion.getId()).isNotNull();
    }

    @DisplayName("아티클 아이디로 디스커션을 조회한다.")
    @Test
    void getDiscussionsByArticleIdTest(){
        //given
        final User user1 = createUserWithProfile("email1@test.com", "username1");
        final User user2 = createUserWithProfile("email2@test.com", "username2");
        final List<User> savedUsers = userRepository.saveAll(List.of(user1, user2));

        final Article article = createArticle(savedUsers.get(0));
        final Article savedArticle = articleRepository.save(article);

        final Discussion discussion1 = createDiscussion(savedUsers.get(0), article, null);
        final Discussion discussion2 = createDiscussion(savedUsers.get(1), article, null);
        final List<Discussion> savedDiscussions1 = discussionRepository.saveAll(List.of(discussion1, discussion2));

        final Discussion discussion3 = createDiscussion(savedUsers.get(0), article, savedDiscussions1.get(0));
        final Discussion discussion4 = createDiscussion(savedUsers.get(1), article, savedDiscussions1.get(0));
        final Discussion discussion5 = createDiscussion(savedUsers.get(0), article, savedDiscussions1.get(1));
        final Discussion discussion6 = createDiscussion(savedUsers.get(1), article, savedDiscussions1.get(1));
        final List<Discussion> savedDiscussions2 = discussionRepository.saveAll(List.of(discussion3, discussion4, discussion5, discussion6));


        //when
        final List<DiscussionResponse> discussionResponses = discussionService.getDiscussions(savedArticle.getId());

        //then
        assertThat(discussionResponses).hasSize(2)
                .extracting("content", "user.id", "user.profile.id")
                .contains(
                        Tuple.tuple(
                                "test content",
                                discussion1.getUser().getId(),
                                discussion1.getUser().getProfile().getId()),
                        Tuple.tuple(
                                "test content",
                                discussion2.getUser().getId(),
                                discussion2.getUser().getProfile().getId())
                );

        assertThat(discussionResponses.get(0).getChildren()).hasSize(2)
                .extracting("content", "user.id", "user.profile.id")
                .contains(
                        Tuple.tuple(
                                "test content",
                                discussion3.getUser().getId(),
                                discussion3.getUser().getProfile().getId()),
                        Tuple.tuple(
                                "test content",
                                discussion4.getUser().getId(),
                                discussion4.getUser().getProfile().getId())
                );
    }

    @DisplayName("디스커션 아이디로 유저를 포함하는 디스커션을 조회한다.")
    @Test
    void findDiscussionWithUserByIdTest(){
        //given
        final User user = createUserWithProfile("eamil1@test.com", "username1");
        final User savedUser = userRepository.save(user);

        final Article article = createArticle(savedUser);
        final Article savedArticle = articleRepository.save(article);

        final Discussion discussion = createDiscussion(savedUser, savedArticle);
        final Discussion savedDiscussion = discussionRepository.save(discussion);

        //when
        final Discussion findDiscussion = discussionService.findDiscussionWithUserById(savedDiscussion.getId());

        //then
        assertThat(findDiscussion.getId()).isEqualTo(savedDiscussion.getId());
        assertThat(findDiscussion.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @DisplayName("존재하지 않는 디스커션 아이디로 조회시 에러를 던진다.")
    @Test
    void findDiscussionByNotExistingId_thenThrowErrorTest(){
        //given
        Long notExistingDiscussionId = 1L;

        //when
        //then
        assertThatThrownBy(() -> discussionService.findDiscussionWithUserById(notExistingDiscussionId))
                .isInstanceOf(DiscussionNotFoundException.class)
                .hasMessage("존재하지 않는 디스커션입니다.");
    }

    @DisplayName("디스커션을 업데이트한다.")
    @Test
    void discussionUpdateTest(){
        //given
        final User user = createUserWithProfile("eamil1@test.com", "username1");
        final User savedUser = userRepository.save(user);

        final Article article = createArticle(savedUser);
        final Article savedArticle = articleRepository.save(article);

        final Discussion discussion = createDiscussion(savedUser, savedArticle);
        final Discussion savedDiscussion = discussionRepository.save(discussion);
        final String content = "updated content";
        final DiscussionUpdateServiceRequest request = DiscussionUpdateServiceRequest.builder().content(content).build();

        //when
        discussionService.updateDiscussion(savedUser.getId(), savedDiscussion.getId(), request);

        //then
        final Optional<Discussion> optionalDiscussion = discussionRepository.findById(savedDiscussion.getId());

        assertThat(optionalDiscussion.isPresent()).isTrue();
        assertThat(optionalDiscussion.get().getContent()).isEqualTo(content);
    }

    @DisplayName("디스커션 업데이트 시 작성자가 아닌 유저가 요청한 경우 에러를 던진다.")
    @Test
    void discussionUpdateRequestByNotWriter_thenThrowErrorTest(){
        //given
        final User user1 = createUserWithProfile("eamil1@test.com", "username1");
        final User user2 = createUserWithProfile("eamil2@test.com", "username2");

        final List<User> savedUsers = userRepository.saveAll(List.of(user1, user2));

        final Article article = createArticle(savedUsers.get(0));
        final Article savedArticle = articleRepository.save(article);

        final Discussion discussion = createDiscussion(savedUsers.get(0), savedArticle);
        final Discussion savedDiscussion = discussionRepository.save(discussion);
        final String content = "updated content";
        final DiscussionUpdateServiceRequest request = DiscussionUpdateServiceRequest.builder().content(content).build();

        //when
        //then
        assertThatThrownBy(() -> discussionService.updateDiscussion(savedUsers.get(1).getId(), savedDiscussion.getId(), request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("접근 권한이 없는 유저입니다.");
    }



    private Discussion createDiscussion(User savedUser, Article savedArticle) {
        return Discussion.builder()
                .user(savedUser)
                .article(savedArticle)
                .content("test content")
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

    private Discussion createDiscussion(User user, Article article, Discussion parent) {
        return Discussion.builder()
                .content("test content")
                .user(user)
                .article(article)
                .parent(parent)
                .build();
    }
}