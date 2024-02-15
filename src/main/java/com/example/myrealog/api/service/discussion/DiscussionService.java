package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.article.ArticleService;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.DiscussionRepository;
import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final UserService userService;
    private final ArticleService articleService;

    @Transactional
    public DiscussionResponse createDiscussion(Long userId,
                                               Long articleId,
                                               DiscussionCreateServiceRequest request) {

        final User findUser = userService.findById(userId);
        final Article findArticle = articleService.findById(articleId);
        final Discussion createdDiscussion = request.toEntity(findUser, findArticle);
        final Discussion savedDiscussion = discussionRepository.save(createdDiscussion);

        return DiscussionResponse.of(savedDiscussion);
    }
}
