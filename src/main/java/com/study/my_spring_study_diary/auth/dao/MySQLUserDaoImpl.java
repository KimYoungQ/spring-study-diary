package com.study.my_spring_study_diary.auth.dao;

import com.study.my_spring_study_diary.auth.entity.User;
import com.study.my_spring_study_diary.auth.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MySQLUserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    // ======================= CREATE =======================

    @Override
    public User save(User user) {
        log.info("Saving User : {}", user.getUsername());

        String sql = """
                INSERT INTO users (email, password, username, role)
                VALUES (?, ?, ?, ?)
                """;

        // KeyHolder: Object to receive auto-generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getRole().name());
            return ps;
        }, keyHolder);

        // Set the generated ID to StudyLog object
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setId(generatedId.longValue());
            log.info("User saved with ID: {}", generatedId.longValue());
        }

        return user;
    }

    /**
     * Save refresh token
     */
    public void saveRefreshToken(Long userId, String token, Timestamp expiresAt) {
        // First, delete any existing refresh tokens for this user
        String deleteSql = "DELETE FROM refresh_tokens WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, userId);

        // Insert new refresh token
        String insertSql = "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, userId, token, expiresAt);
        log.debug("Saved refresh token for user ID: {}", userId);
    }

    // ====================== Read ======================

    @Override
    public Optional<User> findByUsername(String userName) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userName);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find user by refresh token
     */
    public Optional<User> findByRefreshToken(String refreshToken) {
        String sql = """
                SELECT m.* FROM users m
                INNER JOIN refresh_tokens rt ON m.id = rt.user_id
                WHERE rt.token = ? AND rt.expires_at > NOW()
                """;

        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, refreshToken);
            log.debug("Found user by refresh token");
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No user found with valid refresh token");
            return Optional.empty();
        }
    }

    // ====================== Mapper ======================
    private final RowMapper<User> userRowMapper = (rs, rowNum) ->
        User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .username(rs.getString("username"))
                .role(UserRole.valueOf(rs.getString("role")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
}
