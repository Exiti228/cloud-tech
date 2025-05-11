package com.cloud.kinopoisk.service;

import com.cloud.kinopoisk.dto.AddWatched;
import com.cloud.kinopoisk.entity.UserMovieEntity;
import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import com.cloud.kinopoisk.exception.UserMovieNotFound;
import com.cloud.kinopoisk.repository.UserMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMovieService {
    private final UserMovieRepository userMovieRepository;

    public void handleIsWatched(AddWatched addWatched) {
        UserMovieEntity userMovieEntity = userMovieRepository.findById(new EnrollmentId(UUID.fromString(addWatched.getUserId()), UUID.fromString(addWatched.getMovieId())))
                .orElseThrow(() -> new UserMovieNotFound(String.format("Запись о связи userId = %s и movieId = %s не найдена", addWatched.getUserId(), addWatched.getMovieId())));

        userMovieEntity.setIsWatched(true);

        userMovieRepository.save(userMovieEntity);
    }
}
