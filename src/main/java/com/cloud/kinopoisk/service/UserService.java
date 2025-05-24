package com.cloud.kinopoisk.service;

import com.cloud.kinopoisk.dao.User;
import com.cloud.kinopoisk.dto.AddUser;
import com.cloud.kinopoisk.dto.LoginUser;
import com.cloud.kinopoisk.dto.PutUser;
import com.cloud.kinopoisk.entity.UserEntity;
import com.cloud.kinopoisk.exception.UserDataNotMatchedException;
import com.cloud.kinopoisk.exception.UserNotFound;
import com.cloud.kinopoisk.mapper.UserMapper;
import com.cloud.kinopoisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public User handleAddUser(AddUser addUser) {
        UserEntity user = userMapper.dtoToEntity(addUser);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        userRepository.save(user);

        return userMapper.entityToDao(user);
    }

    @Transactional
    public void handleDeleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    public User handleGetUser(String id) {
        UserEntity user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        return userMapper.entityToDao(user);
    }

    @Transactional
    public void handlePutUser(PutUser putUser, String id) {
        UserEntity user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        Optional.ofNullable(putUser)
                .map(PutUser::getLogin)
                .ifPresent(user::setLogin);

        userRepository.save(user);
    }

    @Transactional
    public User compareUser(LoginUser loginUser) {
        UserEntity user = userRepository.findByLoginAndPassword(loginUser.getLogin(), BCrypt.hashpw(loginUser.getPassword(), BCrypt.gensalt()))
                .orElseThrow(() -> new UserDataNotMatchedException("Данные пользователя не совпадают"));

        return new User(user.getId().toString());
    }
}
