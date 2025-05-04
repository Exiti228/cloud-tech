package com.cloud.kinopoisk.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    @JsonProperty(value = "poster_url")
    private String posterUrl;

    private String title;

    private String rating;
}
