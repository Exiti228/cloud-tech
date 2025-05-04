package com.cloud.kinopoisk.dto;

import lombok.Data;

@Data
public class PutMovie {
    private String poster;

    private String title;

    private String rating;

    private String author;
}
