package com.ruby_dev.movieapi.entites;

import com.ruby_dev.movieapi.utils.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Movie extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie title")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie director")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie studio")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie poster")
    private String poster;
}
