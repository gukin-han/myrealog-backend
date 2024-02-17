package com.example.myrealog.api.service.discussion.request;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.discussion.Discussion;
import com.example.myrealog.domain.discussion.Discussion.DiscussionBuilder;
import com.example.myrealog.domain.user.User;
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
        } else if (this.parent.getDepth() == 0) {
            builder.depth(1).parent(this.parent.toEntity(null, null));
        } else {
            throw new IllegalArgumentException("디스커션의 depth 필드는 0 혹은 1만 가능합니다.");
        }
        return builder.build();
    }
}
