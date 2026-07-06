package com.cloud.kinopoisk;

import com.cloud.kinopoisk.dto.*;
import com.cloud.kinopoisk.entity.MovieEntity;
import com.cloud.kinopoisk.entity.UserEntity;
import com.cloud.kinopoisk.entity.UserMovieEntity;
import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import com.cloud.kinopoisk.exception.MovieNotFound;
import com.cloud.kinopoisk.repository.MovieRepository;
import com.cloud.kinopoisk.repository.UserMovieRepository;
import com.cloud.kinopoisk.repository.UserRepository;
import com.cloud.kinopoisk.service.MovieService;
import com.cloud.kinopoisk.service.UserService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
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
public class UserServiceTests {
    @Autowired
    private UserService userService;

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
    void handleAddUserSuccessfully() {
        AddUser user = new AddUser();
        user.setLogin("login");
        user.setPassword("password");

        assertDoesNotThrow(() -> userService.handleAddUser(user));
    }

    @Test
    void handleDeleteSuccessfully() {
        UserEntity user = userRepository.findAll().get(0);

        assertDoesNotThrow(() -> userService.handleDeleteUser(user.getId().toString()));
    }

    @Test
    void handleGetSuccessfully() {
        UserEntity user = userRepository.findAll().get(0);

        assertDoesNotThrow(() -> userService.handleGetUser(user.getId().toString()));
    }

    @Test
    void handlePutUserSuccessfully() {
        UserEntity user = userRepository.findAll().get(0);
        PutUser putUser = new PutUser();
        putUser.setLogin("login");

        assertDoesNotThrow(() -> userService.handlePutUser(putUser, user.getId().toString()));
    }

    @Test
    void compareUserSuccessfully() {
        LoginUser loginUser = new LoginUser();
        loginUser.setLogin("user");
        loginUser.setPassword("password");

        assertDoesNotThrow(() -> userService.compareUser(loginUser));
    }
}
