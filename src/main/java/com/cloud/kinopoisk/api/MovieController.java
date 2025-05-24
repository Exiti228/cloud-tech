package com.cloud.kinopoisk.api;

import com.cloud.kinopoisk.dao.Movie;
import com.cloud.kinopoisk.dto.AddMovie;
import com.cloud.kinopoisk.dto.AddWatched;
import com.cloud.kinopoisk.dto.PutMovie;
import com.cloud.kinopoisk.service.MinioService;
import com.cloud.kinopoisk.service.MovieService;
import com.cloud.kinopoisk.service.UserMovieService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Фильмы",
        description = "Контроллер для управления информации о фильмах",
        externalDocs = @ExternalDocumentation(
                description = "Ссылка на общую документацию",
                url = "https://github.com/Exiti228/cloud-tech"
        )
)
@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    private final MinioService minioService;

    private final UserMovieService userMovieService;

    @Operation(summary = "Добавить фильм", description = "Позволяет добавить фильм в базу данных")
    @PostMapping
    public Movie addMovie(@Valid @RequestBody AddMovie addMovie,
                          @RequestParam(required = false, defaultValue = "pap-s3-storage") @Parameter(description = "Название бакета, куда надо сохранить", example = "pap-s3-storage") String bucket) {
        String posterUrl  = minioService.downloadPoster(addMovie.getPoster(), bucket);
        return movieService.handleAddMovie(addMovie, posterUrl);
    }

    @Operation(summary = "Получить весь список фильмов", description = "Позволяет получить список фильмов")
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.handleGetAllMovies();
    }

    @Operation(summary = "Получить фильм", description = "Позволяет получить фильм")
    @GetMapping(value = "/{id}")
    public Movie getMovie(@PathVariable @Parameter(required = true, description = "UUID фильма для получения", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id) {
        return movieService.handleGetMovie(id);
    }

    @Operation(summary = "Удалить фильм", description = "Позволяет удалить фильм")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable @Parameter(required = true, description = "UUID фильма для удаления", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id) {
        movieService.handleDeleteMovie(id);
    }

    @Operation(summary = "Изменить данные о фильме", description = "Позволяет изменить данные о фильме")
    @PutMapping(value = "/{id}")
    public void putMovie(@RequestBody PutMovie putMovie,
                         @PathVariable @Parameter(required = true, description = "UUID фильма для изменения", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id,
                         @RequestParam(required = false, defaultValue = "pap-s3-storage") @Parameter(description = "Название бакета, куда надо сохранить новый фильм, если он есть", example = "pap-s3-storage") String bucket) {
        movieService.handlePutMovie(putMovie, id, bucket);
    }

    @Operation(summary = "Выставить информацию о просмотренном фильме для переданного пользователя", description = "Позволяет выставить информацию о просмотренном фильме для переданного пользователя")
    @PutMapping
    public void postMovieIsWatched(@Valid @RequestBody AddWatched addWatched) {
        userMovieService.handleIsWatched(addWatched);
    }
}
