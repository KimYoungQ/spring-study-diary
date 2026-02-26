package com.study.my_spring_study_diary.auth.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Refresh Token 관련 필드
    private String refreshToken;
    private LocalDateTime refreshTokenExpiryDate;
}
