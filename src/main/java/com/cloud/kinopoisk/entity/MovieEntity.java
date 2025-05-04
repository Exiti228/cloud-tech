package com.cloud.kinopoisk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "movie")
public class MovieEntity extends BaseObject {
    @Column(name = "poster_url")
    private String posterUrl;

    @Column
    private String title;

    @Column
    private String rating;

    @Column
    private String author;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMovieEntity> users;
}
