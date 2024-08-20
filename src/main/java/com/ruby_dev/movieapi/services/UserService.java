package com.ruby_dev.movieapi.services;


import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.utils.ResetPasswordRequest;
import com.ruby_dev.movieapi.dto.UpdateUserRequest;
import com.ruby_dev.movieapi.dto.UserDto;
import com.ruby_dev.movieapi.dto.UserPageResponse;


import java.util.UUID;

public interface UserService {

    String logoutService(UUID userId);

    UserPageResponse getAllUsersService(String search, Integer pageNumber,
                                       Integer pageSize, String sortBy, String dir);

    UserDto getSingleUserService(UUID userId);

    String updatePasswordService(ResetPasswordRequest passwordRequest, UUID userId);

    UserDto updateUserService(UpdateUserRequest passwordRequest, UUID userId);
}
