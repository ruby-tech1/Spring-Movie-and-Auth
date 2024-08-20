package com.ruby_dev.movieapi.events;

import com.ruby_dev.movieapi.dto.UserForgetMail;
import com.ruby_dev.movieapi.dto.UserVerifyMail;
import com.ruby_dev.movieapi.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserVerifyMail mail){
        emailService.sendVerificationEmail(mail.user(), mail.otp());
    }

    @Async
    @EventListener
    public void handleUserForgetPasswordEvent(UserForgetMail mail){
        emailService.sendForgotPasswordEmail(mail.user(), mail.token());
    }
}
