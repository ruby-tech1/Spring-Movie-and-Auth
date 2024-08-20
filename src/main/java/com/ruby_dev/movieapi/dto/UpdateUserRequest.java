package com.ruby_dev.movieapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 3, max = 200, message="name must be 3 to 200 characters")
    private String name;

    @Size(min = 3, max = 150, message="username must be 3 to 150 characters")
    private String username;

    @Email(message = "Please provide a valid email")
    private String email;
}
