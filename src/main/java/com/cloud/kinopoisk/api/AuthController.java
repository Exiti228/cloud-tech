package com.cloud.kinopoisk.api;

import com.cloud.kinopoisk.dao.User;
import com.cloud.kinopoisk.dto.LoginUser;
import com.cloud.kinopoisk.service.UserService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Авторизация",
        description = "Контроллер для управления авторизацией",
        externalDocs = @ExternalDocumentation(
                description = "Ссылка на общую документацию",
                url = "https://github.com/Exiti228/cloud-tech"
        )
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public User login(@Valid @RequestBody LoginUser loginUser) {
        return userService.compareUser(loginUser);
    }
}
