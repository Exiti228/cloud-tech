package com.cloud.kinopoisk.exception;

public class MovieNotFound extends RuntimeException{
    public MovieNotFound(String message) {
        super(message);
    }
}
