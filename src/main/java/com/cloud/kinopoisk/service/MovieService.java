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
import java.util.Map;
import java.util.stream.Collectors;


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
    public List<Movie> handleGetAllMovies(String userId) {
        UUID userUUID = UUID.fromString(userId);

        List<UserMovieEntity> userMovies = userMovieRepository.findAllByUserId(userUUID);

        Map<UUID, Boolean> watchedMap = userMovies.stream()
                .collect(Collectors.toMap(
                        entry -> entry.getMovie().getId(),
                        UserMovieEntity::getIsWatched
                ));

        return movieRepository.findAll().stream()
                .map(movieEntity -> {
                    Movie movie = movieMapper.entityToDao(movieEntity);
                    movie.setIsWatched(watchedMap.getOrDefault(movieEntity.getId(), false));
                    return movie;
                })
                .toList();
    }


    @Transactional
    public Movie handleGetMovie(String userId, String movieId) {
        UUID userUUID = UUID.fromString(userId);
        UUID movieUUID = UUID.fromString(movieId);

        MovieEntity movieEntity = movieRepository.findById(movieUUID)
                .orElseThrow(() -> new MovieNotFound(String.format("Фильм с id = %s не найден", movieId)));

        Movie movie = movieMapper.entityToDao(movieEntity);

        EnrollmentId id = new EnrollmentId(userUUID, movieUUID);
        boolean watched = userMovieRepository.findById(id)
                .map(UserMovieEntity::getIsWatched)
                .orElse(false);

        movie.setIsWatched(watched);
        return movie;
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

        EnrollmentId enrollmentId = new EnrollmentId(user.getId(), movie.getId());

        UserMovieEntity userMovieEntity = new UserMovieEntity();
        userMovieEntity.setId(enrollmentId);
        userMovieEntity.setUser(user);
        userMovieEntity.setMovie(movie);
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
                .orElseThrow(() -> new MovieNotFound(String.format("Фильм с id = %s не найден", id)));

        Optional.ofNullable(putMovie).map(PutMovie::getAuthor).ifPresent(movie::setAuthor);
        Optional.ofNullable(putMovie).map(PutMovie::getTitle).ifPresent(movie::setTitle);

        Optional.ofNullable(putMovie).map(PutMovie::getPoster).ifPresent(poster -> {
            String objectName = UUID.randomUUID().toString();
            minioService.handleBucketExists(bucket);
            minioService.uploadFile(bucket, objectName, poster);
            minioService.deleteFile(movie.getPosterUrl());

            String posterUrl = minioService.gitFileUrl(bucket, objectName);
            movie.setPosterUrl(posterUrl);
        });

        movieRepository.save(movie);
    }

    @Transactional
    public Movie handleAddMovie(AddMovie addMovie, String posterUrl) {
        MovieEntity movie = movieMapper.addMovieToEntity(addMovie);
        movie.setPosterUrl(posterUrl);
        movieRepository.save(movie);
        return movieMapper.entityToDao(movie);
    }
}
