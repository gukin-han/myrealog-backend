package com.example.myrealog.api.service.article.request;

import com.example.myrealog.domain.article.Article;
import com.example.myrealog.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticlePublishServiceRequest {

    String title;
    String content;
    String excerpt;

    @Builder
    private ArticlePublishServiceRequest(String title, String content, String excerpt) {
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
    }


    public Article toEntity(User user) {
        return Article.builder()
                .user(user).build();
    }
}
