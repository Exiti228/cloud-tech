package com.cloud.kinopoisk.dao;

import lombok.Data;

@Data
public class Movie {
    private String id;

    private String posterUrl;

    private String title;

    private String author;

    private Boolean isWatched;
}
