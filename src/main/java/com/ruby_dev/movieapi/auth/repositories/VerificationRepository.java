package com.ruby_dev.movieapi.auth.repositories;


import com.ruby_dev.movieapi.auth.entities.User;
import com.ruby_dev.movieapi.auth.entities.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {

    @Query("SELECT fp FROM Verification fp WHERE fp.otp = ?1 AND fp.user = ?2")
    Optional<Verification> findByOtpAndUser(String otp, User user);

    Optional<Verification> findByUser(User user);
}
