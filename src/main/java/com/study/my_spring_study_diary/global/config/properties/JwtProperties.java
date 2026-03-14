package com.study.my_spring_study_diary.global.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank(message = "JWT 시크릿 키는 필수입니다")
    private String secret;

    @Min(value = 60, message = "액세스 토큰 만료 시간은 최소 60초 이상이어야 합니다")
    private long accessTokenValidity = 3600;

    @Min(value = 604800, message = "리프레쉬 토큰 만료 시간은 최소 7일 이상이어야 합니다")
    private long refreshTokenExpiry = 604800;
}
