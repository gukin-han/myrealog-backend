package com.example.myrealog.domain.article;

import com.example.myrealog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


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
            WHERE article.articleStatus = 'PUBLIC'
            ORDER BY article.id DESC
           """)
    List<Article> findAllWithUserProfile();

    List<Article> findByUserAndArticleStatus(User user, ArticleStatus articleStatus);
}
