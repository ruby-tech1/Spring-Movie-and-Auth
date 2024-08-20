package com.ruby_dev.movieapi.dto;

import com.ruby_dev.movieapi.auth.entities.User;

public record UserForgetMail(User user, String token) {}
