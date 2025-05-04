package com.cloud.kinopoisk.service;

import com.cloud.kinopoisk.entity.UserEntity;
import com.cloud.kinopoisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(UserEntity user) {
        userRepository.save(user);
    }
}
