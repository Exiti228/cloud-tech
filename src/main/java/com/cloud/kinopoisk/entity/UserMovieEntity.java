package com.cloud.kinopoisk.entity;

import com.cloud.kinopoisk.entity.composite.EnrollmentId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_movie")
@Getter
@Setter
public class UserMovieEntity {
    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne
    @MapsId("userId")
    private UserEntity user;

    @ManyToOne
    @MapsId("movieId")
    private MovieEntity movie;

    private Boolean isWatched = false;
}

