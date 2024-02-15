package com.example.myrealog.domain.article;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArticleStatus {

    PUBLIC("공개"),
    PRIVATE("비공개"),
    DRAFT("초안");

    private final String name;
}
