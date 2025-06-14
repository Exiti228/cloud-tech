package com.cloud.kinopoisk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Сущность добавления пользователя")
@Data
public class AddUser {
    @Schema(description = "Логин пользователя", example = "vova_adidas", required = true)
    @NotBlank
    private String login;

    @Schema(description = "Пароль пользователя", example = "123456", required = true)
    @NotBlank
    private String password;
}
