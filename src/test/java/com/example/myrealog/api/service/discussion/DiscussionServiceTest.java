package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.article.ArticleRepository;
import com.example.myrealog.v1.model.User;
import com.example.myrealog.v1.repository.UserRepository;
import com.example.myrealog.v1.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    void test(){
        //given
        final User user = User.builder().email("email1@test.com").username("username1").build();
        final User savedUser = userRepository.save(user);
        final Article article = Article.builder().title("title1").content("content1").excerpt("excerpt1").user(savedUser).build();
        final Article savedArticle = articleRepository.save(article);
        final DiscussionCreateServiceRequest request = DiscussionCreateServiceRequest.builder().build();

        //when
        final DiscussionResponse discussion = discussionService.createDiscussion(savedUser.getId(), savedArticle.getId(), request);

        //then
        assertThat(discussion.getId()).isNotNull();
    }
}