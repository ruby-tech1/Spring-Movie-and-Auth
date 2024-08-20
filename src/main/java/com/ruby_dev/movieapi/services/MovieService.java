package com.ruby_dev.movieapi.services;

import com.ruby_dev.movieapi.dto.MovieDto;
import com.ruby_dev.movieapi.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(UUID id);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(UUID id, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie (UUID id) throws IOException;

    MoviePageResponse getAllMoviesPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesPaginationAndSorting( String search, Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
