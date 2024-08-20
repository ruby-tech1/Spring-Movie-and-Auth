package com.ruby_dev.movieapi.auth.entities;

import com.ruby_dev.movieapi.utils.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Please provide name")
    @Size(min = 3, max = 200, message="name must be 3 to 200 characters")
    private String name;

    @NotBlank(message = "Please provide username")
    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 150, message="username must be 3 to 150 characters")
    private String usernamee;

    @NotBlank(message = "Please provide email")
    @Column(unique = true, nullable = false)
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Please provide password")
    @Column(nullable = false)
    @Size(min = 6, message="password must be at least 6 or more characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)")
    private String password;

    @OneToOne(mappedBy = "user")
    private Token refreshToken;

    @OneToOne(mappedBy = "user")
    private Verification verification;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private boolean isEnabled = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
//        return true;
    }
}
