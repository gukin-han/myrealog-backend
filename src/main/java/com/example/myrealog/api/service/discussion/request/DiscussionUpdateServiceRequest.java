package com.example.myrealog.api.service.discussion.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiscussionUpdateServiceRequest {

    private String content;

    @Builder
    private DiscussionUpdateServiceRequest(String content) {
        this.content = content;
    }

}
