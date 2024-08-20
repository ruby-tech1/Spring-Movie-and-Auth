package com.ruby_dev.movieapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruby_dev.movieapi.dto.MovieDto;
import com.ruby_dev.movieapi.dto.MoviePageResponse;
import com.ruby_dev.movieapi.exceptions.EmptyFileException;
import com.ruby_dev.movieapi.services.MovieService;
import com.ruby_dev.movieapi.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<MovieDto> createMovie(@RequestPart MultipartFile file,
                                                @Valid @RequestPart String movieDto) throws IOException, EmptyFileException {
        if (file.isEmpty()){
            throw new EmptyFileException("File is empty! Please send another file!");
        }
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<MovieDto>> getAllMovies(){
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getSingleMovie(@PathVariable UUID movieId){
        return new ResponseEntity<>(movieService.getMovie(movieId), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable UUID movieId,
                                                @RequestPart String movieDto,
                                                @RequestPart MultipartFile file) throws IOException {
        if(file.isEmpty()) file = null;
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.updateMovie(movieId, dto, file), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable UUID movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/allMoviePage")
    public ResponseEntity<MoviePageResponse> getAllMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ){
        return new ResponseEntity<>(movieService.getAllMoviesPagination(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/allMoviePageAndSort")
    public ResponseEntity<MoviePageResponse> getAllMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir,
            @RequestParam(defaultValue = AppConstants.SEARCH, required = false) String search
    ){
        return new ResponseEntity<>(movieService.getAllMoviesPaginationAndSorting(search, pageNumber, pageSize, sortBy, dir),
                                    HttpStatus.OK);
    }


    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }

}
