package com.cloud.kinopoisk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMovie {
    @NotBlank
    private String title;

    @NotBlank
    private String poster;

    @NotBlank
    private String author;

    private String rating;
}
