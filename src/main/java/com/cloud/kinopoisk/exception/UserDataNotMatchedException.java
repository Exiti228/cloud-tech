package com.cloud.kinopoisk.exception;

public class UserDataNotMatchedException extends RuntimeException{
    public UserDataNotMatchedException(String message) {
        super(message);
    }
}
