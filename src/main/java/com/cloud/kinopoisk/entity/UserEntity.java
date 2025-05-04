package com.cloud.kinopoisk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity extends BaseObject{
    @Column
    private String login;

    @OneToMany(mappedBy = "user")
    private List<UserMovieEntity> movies;
}
