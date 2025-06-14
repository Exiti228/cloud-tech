package com.cloud.kinopoisk.repository;

import com.cloud.kinopoisk.entity.UserMovieEntity;
import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovieEntity, EnrollmentId> {
    List<UserMovieEntity> findAllByUserId(UUID userId);
}
