package com.ruby_dev.movieapi.repositories;

import com.ruby_dev.movieapi.entites.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Page<Movie> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
}
