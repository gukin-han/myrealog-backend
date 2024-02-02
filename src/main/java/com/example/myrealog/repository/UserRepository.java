package com.example.myrealog.repository;

import com.example.myrealog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndUsername(String email, String username);

    Optional<User> findUserByEmail(String email);

    @Query("select u " +
            "from User u " +
            "join fetch u.profile " +
            "where u.email = :email")
    Optional<User> findUserAndProfileByEmail(@Param("email") String email);

    @Query("select u " +
            "from User u " +
            "join fetch u.profile " +
            "where u.id = :id")
    Optional<User> findUserAndProfileById(@Param("id") Long id);
}
