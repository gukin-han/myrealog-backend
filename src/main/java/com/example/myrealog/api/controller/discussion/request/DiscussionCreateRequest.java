package com.example.myrealog.api.controller.discussion.request;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest.DiscussionCreateServiceRequestBuilder;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussionCreateRequest {

    private int depth;

    @NotBlank(message = "디스커션 내용은 필수입니다.")
    private String content;

    private DiscussionCreateRequest parent;

    @Builder
    private DiscussionCreateRequest(int depth, String content, DiscussionCreateRequest parent) {
        this.depth = depth;
        this.content = content;
        this.parent = parent;
    }

    public DiscussionCreateServiceRequest toServiceRequest() {
        final DiscussionCreateServiceRequestBuilder builder = DiscussionCreateServiceRequest.builder()
                .depth(depth)
                .content(content);

        if (parent != null) {
            builder.parent(parent.toServiceRequest());
        }

        return builder.build();
    }
}
