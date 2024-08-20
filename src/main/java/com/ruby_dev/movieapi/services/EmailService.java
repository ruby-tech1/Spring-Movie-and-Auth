package com.ruby_dev.movieapi.services;

import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.dto.MailBody;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendForgotPasswordEmail(User user, String token);

    void sendVerificationEmail(User user, Integer otp);
}
