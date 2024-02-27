package com.example.myrealog.api.controller.article.request;

import com.example.myrealog.api.service.article.request.ArticlePublishServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleUpdateRequest {

    @NotBlank(message = "아티클 제목은 필수입니다.")
    String title;

    @NotBlank(message = "아티클 내용은 필수입니다.")
    String content;
    String excerpt;

    @Builder
    private ArticleUpdateRequest(String title, String content, String excerpt) {
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
    }

    public ArticlePublishServiceRequest toServiceRequest() {
        return ArticlePublishServiceRequest
                .builder()
                .title(title)
                .content(content)
                .excerpt(excerpt)
                .build();
    }
}
