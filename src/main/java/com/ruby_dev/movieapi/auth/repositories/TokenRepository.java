package com.ruby_dev.movieapi.auth.repositories;

import com.ruby_dev.movieapi.auth.entities.Token;
import com.ruby_dev.movieapi.auth.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUser(User user);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Token t  WHERE t.user = ?1")
//    void deleteToken(User user);
}
