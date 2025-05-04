package com.cloud.kinopoisk.api;

import com.cloud.kinopoisk.dao.Movie;
import com.cloud.kinopoisk.dto.AddMovie;
import com.cloud.kinopoisk.dto.ConnectUserAndMovie;
import com.cloud.kinopoisk.dto.PutMovie;
import com.cloud.kinopoisk.service.MinioService;
import com.cloud.kinopoisk.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    private final MinioService minioService;

    @PostMapping(value = "/movies")
    public void addMovie(@Valid @RequestBody AddMovie addMovie, @RequestParam(required = false, defaultValue = "pap-s3-storage") String bucket) {
        String posterUrl  = minioService.downloadPoster(addMovie.getPoster(), bucket);
        movieService.handleAddMovie(addMovie, posterUrl);
    }

    @PostMapping(value = "/user/movies") // связать
    public void connectUserAndMovie(@Valid @RequestBody ConnectUserAndMovie connectUserAndMovie) {
        movieService.handleConnectUserAndMovie(connectUserAndMovie);
    }

    @GetMapping(value = "/movies")
    public List<Movie> getAllMovies() {
        return movieService.handleGetAllMovies();
    }

    @GetMapping(value = "/movies/{id}")
    public Movie getMovie(@PathVariable String id) {
        return movieService.handleGetMovie(id);
    }

    @DeleteMapping(value = "/movies/{id}")
    public void deleteMovie(@PathVariable String id) {
        movieService.handleDeleteMovie(id);
    }

    @PutMapping(value = "/movies/{id}")
    public void putMovie(@RequestBody PutMovie putMovie, @PathVariable String id, @RequestParam(required = false, defaultValue = "pap-s3-storage") String bucket) {
        movieService.handlePutMovie(putMovie, id, bucket);
    }
}
