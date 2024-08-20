package com.ruby_dev.movieapi.auth.services;

import com.ruby_dev.movieapi.auth.entities.Token;
import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.repositories.TokenRepository;
import com.ruby_dev.movieapi.auth.repositories.UserRepository;
import com.ruby_dev.movieapi.exceptions.TokenExpiredException;
import com.ruby_dev.movieapi.exceptions.TokenNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    @Value("${jwt.refreshToken}")
    private long refreshTimeLimit;

    public TokenService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public Token createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Use not found with username: " + username));

        Token refreshToken = user.getRefreshToken();
        if(refreshToken != null){
            return refreshToken;
        }
        long refreshTokenValidTime = 30*1000;
        refreshToken = Token.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .expireAt(Instant.now().plusMillis(refreshTimeLimit))
                        .user(user)
                        .build();

        tokenRepository.save(refreshToken);
        return refreshToken;
    }

    public Token verifyRefreshToken (String refreshToken) {
        Token refreshTokenDb = tokenRepository.findByRefreshToken(refreshToken)
                                .orElseThrow(() -> new TokenNotFoundException("Refresh Token Not Found"));

        if(refreshTokenDb.getExpireAt().compareTo(Instant.now()) > 0){
            return refreshTokenDb;
        }
        tokenRepository.delete(refreshTokenDb);
        throw new TokenExpiredException("Refresh Token Expired");
    }
}
