package com.cloud.kinopoisk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConnectUserAndMovie {
    @NotBlank
    private String userId;

    @NotBlank
    private String movieId;
}
