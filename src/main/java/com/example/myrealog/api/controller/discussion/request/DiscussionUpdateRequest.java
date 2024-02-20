package com.example.myrealog.api.controller.discussion.request;

import com.example.myrealog.api.service.discussion.request.DiscussionUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussionUpdateRequest {

    @NotBlank(message = "디스커션 내용은 필수입니다.")
    private String content;

    @Builder
    private DiscussionUpdateRequest(String content) {
        this.content = content;
    }

    public DiscussionUpdateServiceRequest toServiceRequest() {
        return DiscussionUpdateServiceRequest.builder()
                .content(content)
                .build();
    }
}
