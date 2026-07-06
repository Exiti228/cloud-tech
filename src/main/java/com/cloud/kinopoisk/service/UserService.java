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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public User handleAddUser(AddUser addUser) {
        if (userRepository.findByLogin(addUser.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким логином уже существует");
        }

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
        UserEntity user = userRepository.findByLogin(loginUser.getLogin())
                .orElseThrow(() -> new UserDataNotMatchedException("Пользователь не найден"));

        if (!BCrypt.checkpw(loginUser.getPassword(), user.getPassword())) {
            throw new UserDataNotMatchedException("Неверный пароль");
        }

        return new User(user.getId().toString(), user.getLogin());
    }
}
