package com.cloud.kinopoisk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Сущность авторизации")
@Data
public class LoginUser {
    @Schema(description = "Логин", example = "abc", required = true)
    @NotBlank
    private String login;

    @Schema(description = "Пароль", example = "abc", required = true)
    @NotBlank
    private String password;
}
