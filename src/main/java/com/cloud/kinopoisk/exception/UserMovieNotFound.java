package com.cloud.kinopoisk.exception;

public class UserMovieNotFound extends RuntimeException{
    public UserMovieNotFound(String message) {
        super(message);
    }
}
