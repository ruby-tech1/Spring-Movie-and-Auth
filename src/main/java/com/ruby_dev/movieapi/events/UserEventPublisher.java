package com.ruby_dev.movieapi.events;

import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.dto.UserForgetMail;
import com.ruby_dev.movieapi.dto.UserVerifyMail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    public final ApplicationEventPublisher eventPublisher;

    public void publishUserRegisteredEvent(User user, Integer otp) {
        eventPublisher.publishEvent(new UserVerifyMail(user, otp));
    }

    public void publishUserForgetPasswordEvent(User user, String token) {
        eventPublisher.publishEvent(new UserForgetMail(user, token));
    }
}
