package com.study.my_spring_study_diary.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Refresh Token Request DTO
 * Contains refresh token for obtaining new access token
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

