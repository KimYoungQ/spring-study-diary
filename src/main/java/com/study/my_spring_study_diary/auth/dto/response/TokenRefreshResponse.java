package com.study.my_spring_study_diary.auth.dto.response;

import lombok.*;

/**
 * Token Response DTO
 * Contains new access token and refresh token
 */
@Value
@Builder
public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;

    /**
     * Create token response with default token type
     */
    public static TokenRefreshResponse of(String accessToken, String refreshToken) {
        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
