package com.cloud.kinopoisk;

import com.cloud.kinopoisk.dto.AddMovie;
import com.cloud.kinopoisk.dto.ConnectUserAndMovie;
import com.cloud.kinopoisk.dto.PutMovie;
import com.cloud.kinopoisk.entity.MovieEntity;
import com.cloud.kinopoisk.entity.UserEntity;
import com.cloud.kinopoisk.entity.UserMovieEntity;
import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import com.cloud.kinopoisk.exception.MovieNotFound;
import com.cloud.kinopoisk.repository.MovieRepository;
import com.cloud.kinopoisk.repository.UserMovieRepository;
import com.cloud.kinopoisk.repository.UserRepository;
import com.cloud.kinopoisk.service.MovieService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
public class MovieServiceTests {
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMovieRepository userMovieRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Mock
    private MinioClient minioClient;

    @PostConstruct
    void post() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        when(minioClient.putObject(any())).thenReturn(null);
        when(minioClient.bucketExists(any())).thenReturn(false);
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @BeforeEach
    void initData() {
        UserEntity user = new UserEntity();
        user.setLogin("user");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);

        MovieEntity movie1 = new MovieEntity();
        movie1.setPosterUrl("http://localhost:9000/bucket/movie1.png");
        movie1.setAuthor("author1");
        movie1.setTitle("title1");
        movie1.setRating("10");

        MovieEntity movie2 = new MovieEntity();
        movie1.setPosterUrl("http://localhost:9000/bucket/movie2.png");
        movie1.setAuthor("author2");
        movie1.setTitle("title2");
        movie1.setRating("10");

        movieRepository.save(movie1);
        movieRepository.save(movie2);

        UserMovieEntity um1 = new UserMovieEntity();
        EnrollmentId enrollmentId1 = new EnrollmentId(user.getId(), movie1.getId());
        um1.setIsWatched(false);
        um1.setMovie(movie1);
        um1.setUser(user);
        um1.setId(enrollmentId1);

        UserMovieEntity um2 = new UserMovieEntity();
        EnrollmentId enrollmentId2 = new EnrollmentId(user.getId(), movie2.getId());
        um2.setIsWatched(true);
        um2.setMovie(movie2);
        um2.setUser(user);
        um2.setId(enrollmentId2);

        userMovieRepository.save(um1);
        userMovieRepository.save(um2);
    }

    @AfterEach
    void clearData() {
        userMovieRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void handleGetAllMoviesSuccessfully() {
        UserEntity user = userRepository.findAll().get(0);

        var movies = movieService.handleGetAllMovies(user.getId().toString());
        assertEquals(2, movies.size());
    }

    @Test
    void handleGetMovieSuccessfully() {
        UserEntity user = userRepository.findAll().get(0);
        MovieEntity movie = movieRepository.findAll().get(0);

        assertDoesNotThrow(() -> movieService.handleGetMovie(user.getId().toString(), movie.getId().toString()));
    }

    @Test
    void handleGetMovieError() {
        UserEntity user = userRepository.findAll().get(0);

        assertThrows(MovieNotFound.class, () -> movieService.handleGetMovie(user.getId().toString(), "9ba48d0b-843d-4a21-981f-3ec9c163e88d"));
    }

    @Test
    void handleConnectUserAndMovieSuccessfully() {
        MovieEntity testMovie = new MovieEntity();
        UserEntity user = userRepository.findAll().get(0);
        testMovie.setPosterUrl("http://localhost:9000/bucket/test.png");
        testMovie.setAuthor("test");
        testMovie.setTitle("test");
        testMovie.setRating("10");
        movieRepository.save(testMovie);

        ConnectUserAndMovie connectDto = new ConnectUserAndMovie();
        connectDto.setMovieId(testMovie.getId().toString());
        connectDto.setUserId(user.getId().toString());

        assertDoesNotThrow(() -> movieService.handleConnectUserAndMovie(connectDto));
    }

    @Test
    void saveSuccessfully() {
        MovieEntity testMovie = new MovieEntity();
        testMovie.setPosterUrl("http://localhost:9000/bucket/test.png");
        testMovie.setAuthor("test");
        testMovie.setTitle("test");
        testMovie.setRating("10");

        assertDoesNotThrow(() -> movieService.save(testMovie));
    }

    @Test
    void deleteSuccessfully() {
        MovieEntity testMovie = movieRepository.findAll().get(0);

        assertDoesNotThrow(() -> movieService.handleDeleteMovie(testMovie.getId().toString()));
    }

    @Test
    void handleAddMovieSuccessfully() {
        AddMovie testMovie = new AddMovie();
        testMovie.setAuthor("test");
        testMovie.setTitle("test");
        testMovie.setRating("10");

        assertDoesNotThrow(() -> movieService.handleAddMovie(testMovie, "poster"));
    }

    @Test
    void handlePutMovieSuccessfully() {
        PutMovie testMovie = new PutMovie();
        MovieEntity movie = movieRepository.findAll().get(0);
        testMovie.setAuthor("test");
        testMovie.setTitle("test");
        testMovie.setRating("10");
        testMovie.setPoster(null);

        assertDoesNotThrow(() -> movieService.handlePutMovie(testMovie, movie.getId().toString(), "film-poster-base"));
    }



}
