package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@Getter
@Table(name = "discussions")
public class Discussion extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "discussion_id")
    private Long id;

    private int depth;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_discussion_id")
    private Discussion parent;

    @OneToMany(mappedBy = "parent")
    private List<Discussion> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "discussion", cascade = {REMOVE}, orphanRemoval = true)
    private List<DiscussionReaction> discussionReactions = new ArrayList<>();
}
