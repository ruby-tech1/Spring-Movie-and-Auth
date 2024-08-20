package com.ruby_dev.movieapi.auth.utils;

public record VerifyUserRequest(Integer otp, String email) {
}
