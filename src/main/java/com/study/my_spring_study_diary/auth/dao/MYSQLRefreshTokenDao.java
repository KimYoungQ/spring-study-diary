package com.study.my_spring_study_diary.auth.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MYSQLRefreshTokenDao implements RefreshTokenDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * saveRefreshToken
     */
    public void saveRefreshToken(Long memberId, String token, Timestamp expiresAt) {
        // First, delete any existing refresh tokens for this user
        String deleteSql = "DELETE FROM refresh_tokens WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, memberId);

        // Insert new refresh token
        String insertSql = "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, memberId, token, expiresAt);
        log.debug("Saved refresh token for user ID: {}", memberId);
    }

    /**
     * Delete refresh token
     */
    public void deleteRefreshToken(String refreshToken) {
        String sql = "DELETE FROM refresh_tokens WHERE token = ?";
        jdbcTemplate.update(sql, refreshToken);
        log.debug("Deleted refresh token");
    }
}
