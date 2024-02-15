package com.example.myrealog.api.service.discussion.response;

import com.example.myrealog.api.service.article.response.ArticleResponse;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.v1.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiscussionResponse {

    private Long id;
    private int depth;
    private String content;
    private DiscussionResponse parent;
    private ArticleResponse article;
    private User user;

    @Builder
    private DiscussionResponse(Long id,
                               int depth,
                               String content,
                               DiscussionResponse parent,
                               ArticleResponse article,
                               User user) {
        this.id = id;
        this.depth = depth;
        this.content = content;
        this.parent = parent;
        this.article = article;
        this.user = user;
    }


    public static DiscussionResponse of(Discussion savedDiscussion) {
        return DiscussionResponse.builder()
                .id(savedDiscussion.getId())
                .depth(savedDiscussion.getDepth())
                .content(savedDiscussion.getContent())
        .build();
    }
}
