package com.cloud.kinopoisk.mapper;

import com.cloud.kinopoisk.dao.Movie;
import com.cloud.kinopoisk.dto.AddMovie;
import com.cloud.kinopoisk.entity.MovieEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie entityToDao(MovieEntity movie);

    MovieEntity addMovieToEntity(AddMovie addMovie);
}
