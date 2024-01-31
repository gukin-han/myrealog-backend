package com.example.myrealog.repository;

import com.example.myrealog.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.myrealog.model.Article.*;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("" +
            "select a " +
            "from Article a " +
            "where a.user.id = :userId " +
            "and a.status = :status")
    List<Article> findByUserIdAndStatus(@Param("userId") Long userId,
                                        @Param("status") Status status);
}
