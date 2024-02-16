package com.example.myrealog.domain.discussion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    @Query("""
            SELECT discussion
            FROM Discussion discussion
            JOIN FETCH discussion.parent parent
            JOIN FETCH discussion.article article
            JOIN FETCH discussion.user user
            JOIN FETCH user.profile profile
            WHERE discussion.article.id = :articleId
           """)
    List<Discussion> findAllByArticleId(@Param("articleId") Long articleId);
}
