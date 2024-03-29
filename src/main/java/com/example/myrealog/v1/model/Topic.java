package com.example.myrealog.v1.model;

import com.example.myrealog.domain.BaseTimeEntity;
import com.example.myrealog.domain.article.Article;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "topics")
public class Topic extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "topic_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "topic")
    private List<Article> articles = new ArrayList<>();
}
