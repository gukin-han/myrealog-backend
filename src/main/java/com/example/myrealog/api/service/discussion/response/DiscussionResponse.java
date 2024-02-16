package com.example.myrealog.api.service.discussion.response;

import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.api.service.user.response.UserResponse;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
public class DiscussionResponse {

    private Long id;
    private int depth;
    private String content;
    private DiscussionResponse parent;
    private UserResponse user;
    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;

    @Builder
    private DiscussionResponse(Long id,
                               int depth,
                               String content,
                               DiscussionResponse parent,
                               UserResponse user,
                               LocalDateTime createdDateTime,
                               LocalDateTime lastModifiedDateTime) {
        this.id = id;
        this.depth = depth;
        this.content = content;
        this.parent = parent;
        this.user = user;
        this.createdDateTime = createdDateTime;
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public static DiscussionResponse of(Discussion savedDiscussion) {
        return new DiscussionResponse(
                savedDiscussion.getId(),
                savedDiscussion.getDepth(),
                savedDiscussion.getContent(),
                savedDiscussion.getParent() == null ? null : DiscussionResponse.of(savedDiscussion.getParent()),
                UserResponse.of(savedDiscussion.getUser()),
                savedDiscussion.getCreatedDateTime(),
                savedDiscussion.getLastModifiedDateTime());
    }
}
