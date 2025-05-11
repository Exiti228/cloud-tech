package com.cloud.kinopoisk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Сущность установки информации о просмотренном фильме для пользователя")
@Data
public class AddWatched {
    @Schema(description = "UUID пользователя", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d", required = true)
    @NotBlank
    private String userId;

    @Schema(description = "UUID фильма", example = "55255367-5dd3-4251-8676-514a1a80b058", required = true)
    @NotBlank
    private String movieId;
}
