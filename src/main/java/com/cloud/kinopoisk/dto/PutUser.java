package com.cloud.kinopoisk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Сущность изменения пользователя")
@Data
public class PutUser {
    @Schema(description = "Логин пользователя", example = "vova_adidas", required = true)
    @NotBlank
    private String login;
}
