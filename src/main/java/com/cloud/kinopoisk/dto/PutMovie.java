package com.cloud.kinopoisk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Сущность изменения информации о фильме")
@Data
public class PutMovie {
    @Schema(description = "Название фильма", example = "Сплит", required = true)
    @NotBlank
    private String title;

    @Schema(description = "Постер фильма в формате base64", example = "Axdwqd==", required = true)
    @NotBlank
    private String poster;

    @Schema(description = "Автор фильма", example = "Балабанов А.О.", required = true)
    @NotBlank
    private String author;

    @Schema(description = "Рейтинг фильма", example = "5")
    private String rating;
}
