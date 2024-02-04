package com.example.myrealog.repository;

import com.example.myrealog.model.Article;
import com.example.myrealog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.myrealog.model.Article.*;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("""
           SELECT a
           FROM Article a
           WHERE a.user.id = :userId
           AND a.status = :status
           """)
    List<Article> findByUserIdAndStatus(@Param("userId") final Long userId,
                                        @Param("status") final Status status);


    @Query("""
           SELECT article
           FROM Article article
           JOIN FETCH article.user user
           JOIN FETCH user.profile profile
           WHERE article.slug = :slug
           AND user.username = :username
           """)
    Optional<Article> findBySlugAndUsername(@Param("slug") final String slug,
                                            @Param("username") final String username);

    @Query("""
            SELECT article
            FROM Article article
            JOIN FETCH article.user user
            WHERE article.id = :articleId
            AND user.id = :userId
            """)
    Optional<Article> findByArticleIdAndUserId(@Param("articleId") final Long articleId,
                                               @Param("userId") final Long userId);

    @Query("""
            SELECT article
            FROM Article article
            JOIN FETCH article.user user
            JOIN FETCH user.profile profile
           """)
    List<Article> findAllWithUserProfile();

}
