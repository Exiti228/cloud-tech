package com.cloud.kinopoisk.api;

import com.cloud.kinopoisk.dao.User;
import com.cloud.kinopoisk.dto.AddUser;
import com.cloud.kinopoisk.dto.ConnectUserAndMovie;
import com.cloud.kinopoisk.dto.PutUser;
import com.cloud.kinopoisk.service.MovieService;
import com.cloud.kinopoisk.service.UserService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Пользователи",
        description = "Контроллер для управления пользователями",
        externalDocs = @ExternalDocumentation(
                description = "Ссылка на общую документацию",
                url = "https://github.com/Exiti228/cloud-tech"
        )
)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final MovieService movieService;

    private final UserService userService;

    @Operation(summary = "Добавить фильм для просмотра пользователю", description = "Позволяет добавить фильм для просмотра пользователю")
    @PostMapping(value = "/movies")
    public void connectUserAndMovie(@Valid @RequestBody ConnectUserAndMovie connectUserAndMovie) {
        movieService.handleConnectUserAndMovie(connectUserAndMovie);
    }

    @Operation(summary = "Создать пользователя", description = "Позволяет создать пользователя")
    @PostMapping
    public User addUser(@Valid @RequestBody AddUser addUser) {
        return userService.handleAddUser(addUser);
    }

    @Operation(summary = "Удалить пользователя", description = "Позволяет удалить пользователя")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Parameter(required = true, description = "UUID пользователя, которого надо удалить", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id) {
        userService.handleDeleteUser(id);
    }

    @Operation(summary = "Получить пользователя", description = "Позволяет получить пользователя")
    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable @Parameter(required = true, description = "UUID пользователя, которого надо получить", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id) {
        return userService.handleGetUser(id);
    }

    @Operation(summary = "Изменить данные пользователя", description = "Позволяет изменить пользователя")
    @PutMapping(value = "/{id}")
    public void putUser(@Valid @RequestBody PutUser putUser,
                        @PathVariable @Parameter(required = true, description = "UUID пользователя, которого надо изменить", example = "9ba48d0b-843d-4a21-981f-3ec9c163e88d") String id) {
        userService.handlePutUser(putUser, id);
    }
}
