package com.ruby_dev.movieapi.dto;

import com.ruby_dev.movieapi.auth.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserDto {

    private UUID id;

    @NotBlank(message = "Please provide name")
    @Size(min = 3, max = 200, message="name must be 3 to 200 characters")
    private String name;

    @NotBlank(message = "Please provide username")
    @Size(min = 3, max = 150, message="username must be 3 to 150 characters")
    private String username;

    @NotBlank(message = "Please provide email")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Please provide password")
    @Size(min = 6, message="password must be at least 6 or more characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?./!,{}()*-]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)")
    private String password;
}
