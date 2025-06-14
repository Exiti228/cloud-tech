package com.cloud.kinopoisk.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Movie {
    private String id;

    @JsonProperty(value = "poster_url")
    private String posterUrl;

    private String title;
    private String author;

    @JsonProperty("isWatched")
    private Boolean isWatched;
}
