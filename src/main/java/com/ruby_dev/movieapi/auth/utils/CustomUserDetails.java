package com.ruby_dev.movieapi.auth.utils;

import com.ruby_dev.movieapi.auth.entities.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final UUID userId;

    public CustomUserDetails(User user){
        super(user.getEmail(), user.getPassword(), user.getAuthorities());
        this.userId = user.getId();
    }
}
