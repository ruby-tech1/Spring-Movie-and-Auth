package com.ruby_dev.movieapi.controllers;

import com.ruby_dev.movieapi.auth.entities.Token;
import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.repositories.UserRepository;
import com.ruby_dev.movieapi.auth.services.AuthService;
import com.ruby_dev.movieapi.auth.services.JwtService;
import com.ruby_dev.movieapi.auth.services.TokenService;
import com.ruby_dev.movieapi.auth.utils.*;
import com.ruby_dev.movieapi.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, TokenService tokenService, JwtService jwtService, UserRepository userRepository) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerController(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(authService.registerService(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginController(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.loginService(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/verifyUser")
    public ResponseEntity<String> verifyUserController(@RequestBody VerifyUserRequest verifyRequest){
        boolean res = authService.verifyUserService(verifyRequest);
        if(!res){
            return new ResponseEntity<>("User not Verified!", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("User Verified!", HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshTokenController(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        Token refreshToken = tokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

       String accessToken = jwtService.generateToken(user);

       return new ResponseEntity<>(AuthResponse.builder()
               .refreshToken(refreshToken.getRefreshToken())
               .accessToken(accessToken)
               .build(), HttpStatus.OK);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPasswordController(@RequestParam String email) {
        return new ResponseEntity<>(authService.forgetPasswordService(email), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPasswordController(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
                                                          @RequestParam String email, @RequestParam String token) {
        boolean res = authService.resetPasswordService(resetPasswordRequest, email, token);
        if(!res){
            return new ResponseEntity<>("Reset Password Failed", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("Reset Password Successful!", HttpStatus.OK);
    }

}
