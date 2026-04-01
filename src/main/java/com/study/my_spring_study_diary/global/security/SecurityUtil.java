package com.study.my_spring_study_diary.global.security;

import com.study.my_spring_study_diary.auth.exception.AuthException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {
    /**
     * 현재 인증된 사용자의 UserDetails 반환
     */
    public static Optional<CustomUserDetails> getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return Optional.of((CustomUserDetails) principal);
        }

        return Optional.empty();
    }

    /**
     * 현재 사용자 ID 반환
     */
    public static Long getCurrentUserId() {
        return getCurrentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new AuthException("인증 정보가 없습니다"));
    }

    /**
     * 현재 사용자 이름 반환
     */
    public static String getCurrentUsername() {
        return getCurrentUser()
                .map(CustomUserDetails::getUsername)
                .orElseThrow(() -> new AuthException("인증 정보가 없습니다"));
    }

    /**
     * 특정 권한 보유 여부 확인
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals(role)))
                .orElse(false);
    }
}
