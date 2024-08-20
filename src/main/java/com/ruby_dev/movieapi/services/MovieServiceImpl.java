package com.ruby_dev.movieapi.services;

import com.ruby_dev.movieapi.dto.MovieDto;
import com.ruby_dev.movieapi.dto.MoviePageResponse;
import com.ruby_dev.movieapi.entites.Movie;
import com.ruby_dev.movieapi.exceptions.FileExistsException;
import com.ruby_dev.movieapi.exceptions.MovieNotFoundException;
import com.ruby_dev.movieapi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService{
    private final MovieRepository movieRepository;

    private final FileService fileService;

    @Value("${project.poster_path}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Upload the file
        if( Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. Set the value of field poster as filename
        movieDto.setPoster(uploadedFileName);

        // 3. Map dto to Movie Object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 4. Save the movie Object -> saved Movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. Generate the poster url
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // 6. Map Movie Object to Dto object and return it
        MovieDto response = new MovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public MovieDto getMovie(UUID id) {
        // 1. Check the data in the database if exists, fetch the data of given id
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        // 2. Generate PosterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. map to MovieDto object and return it
        MovieDto response = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> movieDtos = new ArrayList<>();

        // 2.  iterate through the list, generate poster url for each of the movie object and map to movie dto object
        for(Movie movie: movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(UUID id, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Check if the movie exist with the id
        Movie movie  = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        // 2. If file is null, do nothing with the file
        // if file is not null, then delete existing file associated with the record, and upload the new one
        String fileName = movie.getPoster();
        if(file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        // 3.  set movieDto's poster value, according to step2
        movieDto.setPoster(fileName);

        // 4. map it to movie object
        Movie newMovie = new Movie(
                movie.getId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 5. save the movie object and db -> return saved movie object
        movieRepository.save(newMovie);

        // 6. generate poster Url for it
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 7. map to MovieDto and return it
        MovieDto response = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(UUID id) throws IOException {
        // 1. check if move object exists in DB
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        UUID movieId = movie.getId();

        // 2. Delete the file associated with the object
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        // 3. delete the movie object
        movieRepository.delete(movie);

        movieRepository.deleteById(id);
        return "Movie deleted with id: " + movieId;
    }

    @Override
    public MoviePageResponse getAllMoviesPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        for(Movie movie: movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                                    (int) moviePages.getTotalElements(),
                                    moviePages.getTotalPages(),
                                    moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesPaginationAndSorting(String search, Integer pageNumber,
                                                              Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAllByTitleContainingIgnoreCase(search, pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie: movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(response);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                (int) moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}
