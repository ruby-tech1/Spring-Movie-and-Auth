package com.ruby_dev.movieapi.auth.repositories;

import com.ruby_dev.movieapi.auth.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);

    Page<User> findAllByEmailContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.email = ?2 WHERE u.id = ?1")
    void updateEmail(UUID id, String newEmail);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = ?2 WHERE u.id = ?1")
    void updateName(UUID id, String newName);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.usernamee = ?2 WHERE u.id = ?1")
    void updateUsername(UUID id, String newUsername);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 WHERE u.id = ?1")
    void updatePassword(UUID id, String password);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isEnabled = true WHERE u.id = ?1")
    void enableUser(UUID id);
}
