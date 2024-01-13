package com.example.myrealog.model;

import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "discussion_reactions", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "discussion_id"})})
public class DiscussionReaction extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "discussion_reaction_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
