package com.study.my_spring_study_diary.auth.dao;

import java.sql.Timestamp;

public interface RefreshTokenDao {

    void saveRefreshToken(Long memberId, String token, Timestamp expiresAt);

    void deleteRefreshToken(String refreshToken);
}

