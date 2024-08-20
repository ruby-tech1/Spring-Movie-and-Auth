package com.ruby_dev.movieapi.auth.utils;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    private String oldPassword;

    @NotBlank(message = "Please provide password")
    @Size(min = 6, message="password must be at least 6 or more characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?./!,{}()*-]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)")
    private String password;

    @NotBlank(message = "Please provide password")
    private String repeatPassword;
}
