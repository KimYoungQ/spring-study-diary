package com.study.my_spring_study_diary.global.Security.jwt;

import com.study.my_spring_study_diary.auth.exception.ExpiredTokenException;
import com.study.my_spring_study_diary.auth.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final  String SECRET_KEY = "test-secret-key-for-jwt-token-generation-must-be-longer-than-256-bits";

    @BeforeEach
    void setUP() {
        tokenProvider = new JwtTokenProvider(SECRET_KEY, 1800, 604800);
    }

    @Test
    @DisplayName("액세스 토큰을 생성할 수 있다")
    void createAccessToken_success() {
        //Given
        String username = "testuser";
        String role = "ROLE_USER";

        //When
        String token = tokenProvider.generateAccessToken(username, role);

        //Then
        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("토큰에서 username을 추출할 수 있다")
    void extractUsername_success() {
        // Given
        String username = "testuser";
        String token = tokenProvider.generateAccessToken(username, "ROLE_USER");

        // When
        String extracted = tokenProvider.getUsernameFromToken(token);

        // Then
        // TODO: 추출한 username이 원본과 같은지 검증하세요
        assertThat(extracted).isEqualTo("testuser");
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않다")
    void validateToken_expired() {
        // TODO: 만료 시간이 0인 TokenProvider를 생성하고 토큰 검증 테스트
        // Given
        JwtTokenProvider validateTokenExpriedProvider = new JwtTokenProvider(SECRET_KEY, 0, 0);
        String username = "testUser";
        String role = "ROLE_USER";

        // When
        String token = validateTokenExpriedProvider.generateAccessToken(username, role);

        // Then
        assertThatThrownBy(() -> tokenProvider.validateToken(token))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessage("Token has expired");
    }

    @Test
    @DisplayName("잘못된 시그니처의 토큰은 유효하지 않다")
    void validateToken_invalidSignature() {
        // TODO: 다른 secret key로 생성된 토큰 검증 시 예외 발생 테스트
        // Given
        JwtTokenProvider validTokenWrongSignedProvider = new JwtTokenProvider("aweriuasdifug12312311231231qprjaoisdfjklarewqerasuhiae", 1800, 604800);
        String username = "testUser";
        String role = "ROLE_USER";

        // When
        String token = validTokenWrongSignedProvider.generateAccessToken(username, role);

        // Then
        assertThatThrownBy(() -> tokenProvider.validateToken(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid JWT signature");
    }
}