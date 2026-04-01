package com.study.my_spring_study_diary.global.Security.jwt;

import com.study.my_spring_study_diary.auth.exception.ExpiredTokenException;
import com.study.my_spring_study_diary.auth.exception.InvalidTokenException;
import com.study.my_spring_study_diary.global.config.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 * Generates, validates, and parses JWT tokens using JJWT 0.12.x API
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;
    private long accessTokenValidityInMilliseconds;
    private long refreshTokenValidityInMilliseconds;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        this.accessTokenValidityInMilliseconds =
                jwtProperties.getAccessTokenValidity() * 1000;

        this.refreshTokenValidityInMilliseconds =
                jwtProperties.getRefreshTokenExpiry() * 1000;
    }

    /**
     * Authentication 객체를 기반으로 Access Token 생성
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
    }

    /**
     * username과 roles를 기반으로 Access Token 생성
     */
    public String generateAccessToken(Long id, String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(username)              // 토큰의 주체 (사용자 식별자)
                .claim("id", id)
                .claim("roles", roles)          // 권한 정보
                .claim("type", "access")        // 토큰 타입 구분
                .issuedAt(now)                  // 발급 시간
                .expiration(expiryDate)         // 만료 시간
                .signWith(secretKey)            // 서명
                .compact();
    }

    /**
     * username과 roles를 기반으로 Access Token 생성
     */
    public String generateAccessToken(String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(username)              // 토큰의 주체 (사용자 식별자)
                .claim("roles", roles)          // 권한 정보
                .claim("type", "access")        // 토큰 타입 구분
                .issuedAt(now)                  // 발급 시간
                .expiration(expiryDate)         // 만료 시간
                .signWith(secretKey)            // 서명
                .compact();
    }

    /**
     * username을 기반으로 Refresh Token 생성
     * Refresh Token에는 roles 정보를 포함하지 않는다.
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")       // 토큰 타입 구분
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 username 추출
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }

    /**
     * 토큰에서 권한(roles) 정보 추출
     */
    public String getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("roles", String.class);
        } catch (Exception e) {
            log.error("Failed to extract roles from token", e);
            return "";
        }
    }

    /**
     * 토큰 유효성 검증
     * 서명이 올바른지, 만료되지 않았는지 등을 확인한다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token has expired", e);
            throw new ExpiredTokenException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new InvalidTokenException("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format", e);
            throw new InvalidTokenException("Invalid JWT token format");
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
            throw new InvalidTokenException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
            throw new InvalidTokenException("JWT claims string is empty");
        }
    }

    /**
     * Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            log.error("Failed to check token type", e);
            return false;
        }
    }

    /**
     * 토큰의 만료 시간 추출
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Failed to extract expiration from token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }
}

