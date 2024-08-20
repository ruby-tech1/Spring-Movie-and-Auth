package com.ruby_dev.movieapi.controllers;

import com.ruby_dev.movieapi.auth.utils.CustomUserDetails;
import com.ruby_dev.movieapi.auth.utils.ResetPasswordRequest;
import com.ruby_dev.movieapi.dto.UpdateUserRequest;
import com.ruby_dev.movieapi.dto.UserDto;
import com.ruby_dev.movieapi.dto.UserPageResponse;
import com.ruby_dev.movieapi.services.UserService;
import com.ruby_dev.movieapi.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.logoutService(userDetails.getUserId()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<UserPageResponse> getAllUserController(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = "name", required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir,
            @RequestParam(defaultValue = AppConstants.SEARCH, required = false) String search
    ) {
        return new ResponseEntity<>(userService.getAllUsersService(search, pageNumber, pageSize, sortBy, dir),
                                    HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserController(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getSingleUserService(userId), HttpStatus.OK);
    }

    @GetMapping("/showCurrentUser")
    public ResponseEntity<UserDto> getUserController(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getSingleUserService(userDetails.getUserId()), HttpStatus.OK);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePasswordController(@Valid @RequestBody ResetPasswordRequest passwordRequest,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.updatePasswordService(passwordRequest, userDetails.getUserId()),
                                    HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<UserDto> updateUserController(@Valid @RequestBody UpdateUserRequest passwordRequest,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(userService.updateUserService(passwordRequest, userDetails.getUserId()),
                                    HttpStatus.OK);
    }
}
