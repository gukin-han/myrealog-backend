package com.example.myrealog.dto.request;

import lombok.Data;

@Data
public class ArticlePublishFormRequest {

    String title;
    String content;
    String excerpt;
}
