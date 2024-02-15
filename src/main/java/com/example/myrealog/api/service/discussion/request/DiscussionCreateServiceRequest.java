package com.example.myrealog.api.service.discussion.request;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.Discussion.DiscussionBuilder;
import com.example.myrealog.v1.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussionCreateServiceRequest {

    private Long id;
    private int depth;
    private String content;
    private DiscussionCreateServiceRequest parent;

    @Builder
    private DiscussionCreateServiceRequest(Long id, int depth, String content, DiscussionCreateServiceRequest parent) {
        this.id = id;
        this.depth = depth;
        this.content = content;
        this.parent = parent;
    }

    public Discussion toEntity(User user, Article article) {

        final DiscussionBuilder builder = Discussion.builder()
                .id(this.id)
                .content(this.content)
                .user(user)
                .article(article);

        if (this.parent == null) {
            builder.depth(0);
        } else {
            builder.depth(this.parent.getDepth() + 1)
                    .parent(this.parent.toEntity(null, null));
        }
        return builder.build();
    }
}
