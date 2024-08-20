package com.ruby_dev.movieapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private UUID id;

    @NotBlank(message = "Please provide movie title")
    private String title;

    @NotBlank(message = "Please provide movie director")
    private String director;

    @NotBlank(message = "Please provide movie studio")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Please provide movie poster")
    private String poster;

    @NotBlank(message = "Please provide movie poster url")
    private String posterUrl;
}
