package com.cloud.kinopoisk.service;

import com.cloud.kinopoisk.dao.Movie;
import com.cloud.kinopoisk.dto.AddMovie;
import com.cloud.kinopoisk.dto.ConnectUserAndMovie;
import com.cloud.kinopoisk.dto.PutMovie;
import com.cloud.kinopoisk.entity.MovieEntity;
import com.cloud.kinopoisk.entity.UserEntity;
import com.cloud.kinopoisk.entity.UserMovieEntity;
import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import com.cloud.kinopoisk.exception.MovieNotFound;
import com.cloud.kinopoisk.exception.UserNotFound;
import com.cloud.kinopoisk.mapper.MovieMapper;
import com.cloud.kinopoisk.repository.MovieRepository;
import com.cloud.kinopoisk.repository.UserMovieRepository;
import com.cloud.kinopoisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;

    private final UserRepository userRepository;

    private final UserMovieRepository userMovieRepository;

    private final MovieMapper movieMapper;

    private final MinioService minioService;

    @Transactional
    public List<Movie> handleGetAllMovies() {
        return movieRepository.findAll()
                .stream().map(movieMapper::entityToDao).toList();
    }

    @Transactional
    public Movie handleGetMovie(String id) {
        return movieRepository.findById(UUID.fromString(id))
                .map(movieMapper::entityToDao)
                .orElseThrow(() -> new MovieNotFound(String.format("Фильм с id = %s не найден", id)));
    }

    @Transactional
    public void save(MovieEntity movie) {
        movieRepository.save(movie);
    }

    @Transactional
    public void handleConnectUserAndMovie(ConnectUserAndMovie connectUserAndMovie) {
        MovieEntity movie = movieRepository.findById(UUID.fromString(connectUserAndMovie.getMovieId()))
                .orElseThrow(() -> new MovieNotFound(String.format("Фильм с id = %s не найден", connectUserAndMovie.getMovieId())));

        UserEntity user = userRepository.findById(UUID.fromString(connectUserAndMovie.getUserId()))
                .orElseThrow(() -> new UserNotFound(String.format("Пользователь с id = %s не найден", connectUserAndMovie.getUserId())));

        EnrollmentId enrollmentId = new EnrollmentId();
        enrollmentId.setMovieId(movie.getId());
        enrollmentId.setUserId(user.getId());

        UserMovieEntity userMovieEntity = new UserMovieEntity();
        userMovieEntity.setId(enrollmentId);
        userMovieEntity.setMovie(movie);
        userMovieEntity.setUser(user);
        userMovieEntity.setIsWatched(false);

        userMovieRepository.save(userMovieEntity);
    }

    @Transactional
    public void handleDeleteMovie(String id) {
        movieRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    public void handlePutMovie(PutMovie putMovie, String id, String bucket) {
        MovieEntity movie = movieRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new MovieNotFound(String.format("Фильм с id = %s не найден", id))); // Тут транзакция, от нее не уйти

        Optional.ofNullable(putMovie).map(PutMovie::getAuthor).ifPresent(movie::setAuthor);

        Optional.ofNullable(putMovie).map(PutMovie::getRating).ifPresent(movie::setRating);

        Optional.ofNullable(putMovie).map(PutMovie::getTitle).ifPresent(movie::setTitle);

        Optional.ofNullable(putMovie).map(PutMovie::getPoster).ifPresent(poster -> {
            String objectName = UUID.randomUUID().toString();
            minioService.handleBucketExists(bucket);

            minioService.uploadFile(bucket, objectName, putMovie.getPoster());

            String posterUrl = minioService.gitFileUrl(bucket, objectName);

            minioService.deleteFile(movie.getPosterUrl());

            movie.setPosterUrl(posterUrl);

        });
    }

    @Transactional
    public Movie handleAddMovie(AddMovie addMovie, String posterUrl) {
        MovieEntity movie = movieMapper.addMovieToEntity(addMovie);

        movie.setPosterUrl(posterUrl);

        movieRepository.save(movie);

        return movieMapper.entityToDao(movie);
    }
}
