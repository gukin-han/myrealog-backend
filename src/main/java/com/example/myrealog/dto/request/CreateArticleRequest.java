package com.example.myrealog.dto.request;

import com.example.myrealog.model.ArticleStatus;
import lombok.Data;

@Data
public class CreateArticleRequest {
    private String title;
    private String content;
    private String slug;
    private String featuredImageUrl;
    private String metaDescription;
    private ArticleStatus status;
}
