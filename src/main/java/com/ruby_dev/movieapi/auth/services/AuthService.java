package com.ruby_dev.movieapi.auth.services;

import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.entities.UserRole;
import com.ruby_dev.movieapi.auth.entities.Verification;
import com.ruby_dev.movieapi.auth.repositories.UserRepository;
import com.ruby_dev.movieapi.auth.repositories.VerificationRepository;
import com.ruby_dev.movieapi.auth.utils.*;
import com.ruby_dev.movieapi.dto.UserDto;
import com.ruby_dev.movieapi.events.UserEventListener;
import com.ruby_dev.movieapi.events.UserEventPublisher;
import com.ruby_dev.movieapi.services.EmailService;
import com.ruby_dev.movieapi.utils.HashingUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final VerificationRepository verificationRepository;
    private final UserEventPublisher userEventPublisher;


    public String registerService(UserDto userDto) {
        var user = User.builder()
                .name(userDto.getName())
                .usernamee(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        Integer otp = generateOTP();
        long timeLimit = 15 * 60 * 1000;

        Verification verification = Verification.builder()
                .otp(HashingUtility.createHash(String.valueOf(otp)))
                .expireAt(Instant.now().plusSeconds(timeLimit))
                .user(savedUser)
                .build();

        verificationRepository.save(verification);
        userEventPublisher.publishUserRegisteredEvent(user, otp);


        return "Check your email to verify your account";
    };

    public Boolean verifyUserService(VerifyUserRequest verifyRequest) {
        User user = userRepository.findByEmail(verifyRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Credentials"));

        String tempToken = HashingUtility.createHash(String.valueOf(verifyRequest.otp()));
        Verification verification = verificationRepository.findByOtpAndUser(tempToken, user)
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if(verification.getExpireAt().isBefore(Instant.now())){
            verificationRepository.delete(verification);
            return false;
        }

        userRepository.enableUser(user.getId());
        verificationRepository.delete(verification);
        return true;
    }

    public AuthResponse loginService(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword())
        );
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Credentials"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = tokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public String forgetPasswordService(String email){
        Optional<User> tempUser = userRepository.findByEmail(email);
        if(tempUser.isEmpty()) {
            return "Email to reset password sent";
        }

        User user = tempUser.get();
        if(!user.isEnabled()){
            return "Email to reset password sent";
        }

        verificationRepository.findByUser(user).ifPresent(verificationRepository::delete);

        String token = UUID.randomUUID().toString();
        long timeLimit = 15 * 60 * 1000;

        Verification verification = Verification.builder()
                .otp(HashingUtility.createHash(token))
                .expireAt(Instant.now().plusSeconds(timeLimit))
                .user(user)
                .build();

        userEventPublisher.publishUserForgetPasswordEvent(user, token);
        verificationRepository.save(verification);

        return "Email to reset password sent";
    }

    public Boolean resetPasswordService(ResetPasswordRequest resetRequest, String email, String token){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Credentials"));

        String tempToken = HashingUtility.createHash(token);
        Verification verification = verificationRepository.findByOtpAndUser(tempToken, user)
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if(verification.getExpireAt().isBefore(Instant.now())){
            verificationRepository.delete(verification);
            return false;
        }

        if(!Objects.equals(resetRequest.getPassword(), resetRequest.getRepeatPassword())) {
            return false;
        }

        String password = passwordEncoder.encode(resetRequest.getPassword());
        userRepository.updatePassword(user.getId(), password);
        verificationRepository.delete(verification);
        return true;
    }

    private Integer generateOTP() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
