package com.ruby_dev.movieapi.services;

import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.dto.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String userEmail;

    private JavaMailSender javaMailSender;

    @Value("${frontend.url}")
    private String origin;

    private EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    private void sendSimpleMessage(MailBody mailBody){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(userEmail);
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);
    }


    private void sendMessage(MailBody mailBody)  {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(mailBody.to());
            helper.setFrom(userEmail);
            helper.setSubject(mailBody.subject());
            helper.setText(mailBody.text(), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error in sending email");
        }
    }

    @Override
    public void sendForgotPasswordEmail(User user, String token)  {
        String resetUrl = origin + "/user/reset-password?token=" + token + "&email=" + user.getEmail();
        String htmlText = "<h4>Hello, " + user.getName() + "<h4> <p>Please reset password by clicking the link: <a href=\""
                            + resetUrl +"\">Reset Password</a></p>";

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Reset Password")
                .text(htmlText)
                .build();

        sendMessage(mailBody);
    }

    @Override
    public void sendVerificationEmail(User user, Integer otp) {
        String htmlText = "<h4>Hello, " + user.getName() + "<h4> <p>Email Verfication OTP: " + otp + "</p>";

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .subject("Account Verification")
                .text(htmlText)
                .build();

        sendMessage(mailBody);
    }

}
