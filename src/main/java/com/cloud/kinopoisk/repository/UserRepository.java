package com.cloud.kinopoisk.repository;

import com.cloud.kinopoisk.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLoginAndPassword(String login, String password);
    Optional<UserEntity> findByLogin(String login);
}

