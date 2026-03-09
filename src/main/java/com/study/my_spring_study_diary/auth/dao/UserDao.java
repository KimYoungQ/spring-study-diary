package com.study.my_spring_study_diary.auth.dao;

import com.study.my_spring_study_diary.auth.entity.User;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserDao {

    // ========== CREATE ==========
    User save(User user);

    void saveRefreshToken(Long userId, String token, Timestamp expiresAt);

    // ========== READ ==========
    Optional<User> findByUsername(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);
}
