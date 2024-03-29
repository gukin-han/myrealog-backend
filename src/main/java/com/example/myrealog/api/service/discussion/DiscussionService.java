package com.example.myrealog.api.service.discussion;

import com.example.myrealog.api.service.article.ArticleService;
import com.example.myrealog.api.service.discussion.request.DiscussionUpdateServiceRequest;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.DiscussionRepository;
import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.api.service.user.UserService;
import com.example.myrealog.v1.common.exception.DiscussionNotFoundException;
import com.example.myrealog.v1.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<DiscussionResponse> getDiscussions(Long articleId) {
        final List<Discussion> discussions = discussionRepository.findAllByArticleId(articleId);
        final List<DiscussionResponse> discussionResponses = discussions.stream()
                .map(DiscussionResponse::of)
                .toList();

        return discussionResponseTreeBuilder(discussionResponses);
    }


    private List<DiscussionResponse> discussionResponseTreeBuilder(List<DiscussionResponse> discussionResponses) {
        final Map<Long, DiscussionResponse> idToEntityMap = createEntityMapBy(discussionResponses);
        addChildrenToParent(discussionResponses, idToEntityMap);
        return filterRootDiscussion(discussionResponses);
    }

    private Map<Long, DiscussionResponse> createEntityMapBy(List<DiscussionResponse> discussionResponses) {
        return discussionResponses.stream()
                .collect(Collectors.toMap(DiscussionResponse::getId, discussionResponse -> discussionResponse));
    }

    private void addChildrenToParent(List<DiscussionResponse> discussionResponses, Map<Long, DiscussionResponse> idToEntityMap) {
        discussionResponses.forEach(discussionResponse -> {
            if (hasParent(discussionResponse)) {
                final Long parentId = discussionResponse.getParent().getId();
                final DiscussionResponse parent = idToEntityMap.get(parentId);
                parent.getChildren().add(discussionResponse);
            }
        });
    }

    private List<DiscussionResponse> filterRootDiscussion(List<DiscussionResponse> discussionResponses) {
        return discussionResponses.stream()
                .filter(discussionResponse -> !hasParent(discussionResponse))
                .toList();
    }

    private boolean hasParent(DiscussionResponse discussionResponse) {
        return discussionResponse.getParent() != null;
    }

    @Transactional
    public DiscussionResponse updateDiscussion(Long userId,
                                               Long discussionId,
                                               DiscussionUpdateServiceRequest request) {

        final User findUser = userService.findById(userId);
        final Discussion findDiscussion = findDiscussionWithUserById(discussionId);
        validateIfRequestUserIsDiscussionWriter(findUser, findDiscussion);

        final Discussion updatedDiscussion = findDiscussion.updateContent(request.getContent());
        return DiscussionResponse.of(updatedDiscussion);
    }

    private void validateIfRequestUserIsDiscussionWriter(User findUser, Discussion findDiscussion) {
        if (findDiscussion.getUser().getId() != findUser.getId()) {
            throw new UnauthorizedException();
        }
    }

    @Transactional
    public Discussion findDiscussionWithUserById(Long discussionId) {
        return discussionRepository.findDiscussionWithUserById(discussionId)
                .orElseThrow(DiscussionNotFoundException::new);
    }

    @Transactional
    public void deleteDiscussion(Long userId, Long discussionId) {
        final User findUser = userService.findById(userId);
        final Discussion findDiscussion = findDiscussionWithUserById(discussionId);
        validateIfRequestUserIsDiscussionWriter(findUser, findDiscussion);
        discussionRepository.delete(findDiscussion);
    }
}
