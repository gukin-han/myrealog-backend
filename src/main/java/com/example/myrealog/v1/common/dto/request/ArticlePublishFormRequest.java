package com.example.myrealog.v1.common.dto.request;

import lombok.Data;

@Data
public class ArticlePublishFormRequest {

    String title;
    String content;
    String excerpt;
}
