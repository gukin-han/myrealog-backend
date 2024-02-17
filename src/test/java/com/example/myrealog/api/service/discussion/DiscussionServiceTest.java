package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.DiscussionRepository;
import com.example.myrealog.domain.profile.Profile;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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