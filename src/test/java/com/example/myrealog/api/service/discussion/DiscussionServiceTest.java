package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
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

    @DisplayName("디스커션을 생성한다.")
    @Test
    void createDiscussionTest(){
        //given
        final Profile profile1 = Profile.builder().displayName("displayName").build();
        final User user = User.builder().email("email1@test.com").username("username1").profile(profile1).build();
        final User savedUser = userRepository.save(user);
        final Article article = Article.builder().title("title1").content("content1").excerpt("excerpt1").user(savedUser).build();
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
        final Profile profile1 = Profile.builder().displayName("displayName").build();
        final User user1 = User.builder().email("email1@test.com").username("username1").profile(profile1).build();
        final User savedUser1 = userRepository.save(user1);

        final Profile profile2 = Profile.builder().displayName("displayName").build();
        final User user2 = User.builder().email("email2@test.com").username("username2").profile(profile2).build();
        final User savedUser2 = userRepository.save(user2);

        final Article article = Article.builder().title("title1").content("content1").excerpt("excerpt1").user(savedUser1).build();
        final Article savedArticle = articleRepository.save(article);

        final DiscussionCreateServiceRequest request = DiscussionCreateServiceRequest.builder().content("test content").build();
        final Long articleId = savedArticle.getId();
        final DiscussionResponse discussion1 = discussionService.createDiscussion(savedUser1.getId(), articleId, request);
        final DiscussionResponse discussion2 = discussionService.createDiscussion(savedUser2.getId(), articleId, request);

        //when
        final List<DiscussionResponse> discussions = discussionService.getDiscussions(articleId);

        //then
        assertThat(discussions).hasSize(2)
                .extracting("content", "user.id", "user.profile.id")
                .contains(
                        Tuple.tuple("test content", discussion1.getUser().getId(), discussion1.getUser().getProfile().getId()),
                        Tuple.tuple("test content", discussion2.getUser().getId(), discussion2.getUser().getProfile().getId())
                );
    }
}