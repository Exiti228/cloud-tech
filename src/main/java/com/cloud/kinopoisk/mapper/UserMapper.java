package com.cloud.kinopoisk.mapper;

import com.cloud.kinopoisk.dao.User;
import com.cloud.kinopoisk.dto.AddUser;
import com.cloud.kinopoisk.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity dtoToEntity(AddUser user);

    User entityToDao(UserEntity user);
}
