package com.ruby_dev.movieapi.services;

import com.ruby_dev.movieapi.auth.entities.Token;
import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.repositories.TokenRepository;
import com.ruby_dev.movieapi.auth.repositories.UserRepository;
import com.ruby_dev.movieapi.auth.utils.ResetPasswordRequest;
import com.ruby_dev.movieapi.dto.UpdateUserRequest;
import com.ruby_dev.movieapi.dto.UserDto;
import com.ruby_dev.movieapi.dto.UserPageResponse;
import com.ruby_dev.movieapi.exceptions.TokenNotFoundException;
import com.ruby_dev.movieapi.exceptions.UserNotFoundException;
import com.ruby_dev.movieapi.utils.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;


    @Override
    public String logoutService(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Unauthorized"));

        Token token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new TokenNotFoundException("Unauthorized"));

        tokenRepository.delete(token);
        return "Logout Successful";
    }

    @Override
    public UserPageResponse getAllUsersService(String search, Integer pageNumber,
                                         Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort );

        Page<User> userPages = null;
        if(EmailValidator.isEmailValid(search)){
            userPages = userRepository.findAllByEmailContainingIgnoreCase(search, pageable);
        }else {
            userPages = userRepository.findAllByNameContainingIgnoreCase(search, pageable);
        }

        List<User> users = userPages.getContent();
        List<UserDto> userDtos = new ArrayList<>();

        for(User user : users){
            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .username(user.getUsernamee())
                    .build();
            userDtos.add(userDto);
        }

        return new UserPageResponse(userDtos, pageNumber, pageSize,
                                    userPages.getNumberOfElements(),
                                    userPages.getTotalPages(), userPages.isLast());
    }

    @Override
    public UserDto getSingleUserService(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsernamee())
                .build();
    }


    @Override
    public String updatePasswordService(ResetPasswordRequest passwordRequest, UUID userId) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Invalid Credentials"));

        if(!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Old Password");
        }

        if(!Objects.equals(passwordRequest.getPassword(), passwordRequest.getRepeatPassword())){
            throw new RuntimeException("New Password don't match");
        }

        String newPassword = passwordEncoder.encode(passwordRequest.getPassword());
        userRepository.updatePassword(user.getId(), newPassword);

        return "Password Changed Successfully";
    }


    @Override
    public UserDto updateUserService(UpdateUserRequest updateRequest, UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Invalid Credentials"));

        if(updateRequest.getName() != null){
            userRepository.updateName(userId, updateRequest.getName());
        }

        if(updateRequest.getUsername() != null){
            userRepository.updateUsername(userId, updateRequest.getUsername());
        }

        if(updateRequest.getEmail() != null){
            userRepository.updateEmail(userId, updateRequest.getEmail());
        }

        // The returned object is not the update one fix it later
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Invalid Credentials"));

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsernamee())
                .email(user.getEmail())
                .build();
    }


}
