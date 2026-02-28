package com.study.my_spring_study_diary.global.Security.jwt;

import com.study.my_spring_study_diary.auth.exception.ExpiredTokenException;
import com.study.my_spring_study_diary.auth.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. 요청 헤더에서 JWT 토큰 추출
            // 예: "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdW..." → "eyJhbGciOiJIUzUxMiJ9.eyJzdW..."
            String token = extractTokenFromRequest(request);

            // 2. 토큰이 존재하면 인증 수행
            if (token != null) {
                authenticateUser(token, request);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            // JWT 관련 예외는 request에 저장하여 EntryPoint에서 처리
            log.error("JWT authentication failed: {}", e.getMessage());
            request.setAttribute("exception", e);
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication", e);
            request.setAttribute("exception", e);
        }

        // 3. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 인증이 필요하지 않은 경로는 필터를 건너뛴다.
     * /api/auth/ 하위 경로(로그인, 회원가입 등)는 토큰 검증이 불필요하다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 토큰 값만 추출
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // "Bearer eyJhbGciOiJIUzUxMiJ9..." → "eyJhbGciOiJIUzUxMiJ9..."

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * JWT 토큰을 검증하고, 유효하면 SecurityContext에 인증 정보를 설정
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        // Validate token
        if (jwtTokenProvider.validateToken(token)) {
            // Extract username from token
            // 로그인할 때, Access Token을 만들 때, 입력했던 or 넣었던 값들 중에
            // username 꺼내기 메서드
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Extract roles from token
            // Role(권한 정보) 꺼내기 메서드
            String rolesString = jwtTokenProvider.getRolesFromToken(token);
            List<SimpleGrantedAuthority> authorities = parseAuthorities(rolesString);

            // Create UserDetails
            // Spring Security에서 사용하는 UserDetails 인터페이스를 구현한 User 객체 생성
            UserDetails userDetails = User.builder()
                    .username(username)
                    .password("") // Password is not needed for JWT authentication
                    .authorities(authorities)
                    .build();

            // Create authentication token
            // 사용자 정보 or 인증된 사용자의 정보를 담고 있는 DTO
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // Set additional details
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            // SecurityContext에 저장한다.
            // 이렇게 저장을 해주는 이유는 -> "Spring Boot 영역에서 비즈니스 로직을 실행할 때, 유저의 정보가 필요한 경우,
            // 손쉽게 빼서 사용할 수 있도록 하기 위해서"
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user: {}", username);
        }
    }

    /**
     * 쉼표로 구분된 roles 문자열을 GrantedAuthority 리스트로 변환
     * 예: "ROLE_USER,ROLE_ADMIN" → [SimpleGrantedAuthority("ROLE_USER"), SimpleGrantedAuthority("ROLE_ADMIN")]
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String rolesString) {
        if (rolesString == null || rolesString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rolesString.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
