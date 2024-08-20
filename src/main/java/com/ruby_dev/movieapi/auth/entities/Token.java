package com.ruby_dev.movieapi.auth.entities;

import com.ruby_dev.movieapi.utils.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Token extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Please provide refresh token")
    private String refreshToken;

    @Column(nullable = false)
    private Instant expireAt;

    @OneToOne
    private User user;
}
