package com.example.myrealog.domain.discussion;

import com.example.myrealog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    @Query("""
            SELECT discussion
            FROM Discussion discussion
            LEFT JOIN FETCH discussion.parent parent
            LEFT JOIN FETCH discussion.article article
            LEFT JOIN FETCH discussion.user user
            LEFT JOIN FETCH user.profile profile
            WHERE discussion.article.id = :articleId
           """)
    List<Discussion> findAllByArticleId(@Param("articleId") Long articleId);


    @Query("""
            SELECT discussion
            FROM Discussion discussion
            JOIN FETCH discussion.user user
            WHERE discussion.id = :discussionId
           """)
    Optional<Discussion> findDiscussionWithUserById(@Param("discussionId") Long discussionId);

}
