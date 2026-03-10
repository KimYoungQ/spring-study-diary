package com.study.my_spring_study_diary.global.Security.config;

import com.study.my_spring_study_diary.global.Security.jwt.JwtAuthenticationEntryPoint;
import com.study.my_spring_study_diary.global.Security.jwt.JwtAuthenticationFilter;
import com.study.my_spring_study_diary.global.filter.MdcLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ① REST API이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // ② JWT 방식이므로 세션을 사용하지 않음 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ③ 요청별 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/refresh",
                                "/api/auth/logout"
                        ).permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // H2 console (only for test profile)
                        .requestMatchers("/h2-console/**").permitAll()

                        .anyRequest().authenticated()                   // 나머지는 인증 필요
                )

                // ④ 인증 실패 시 커스텀 EntryPoint 사용
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // ④-1 H2 콘솔 iframe 허용 (X-Frame-Options 설정)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )

                // ⑤ JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // ⑥ MDC 필터를 JWT 필터 뒤에 배치 (인증 정보 설정 후 실행)
                .addFilterAfter(new MdcLoggingFilter(),
                        JwtAuthenticationFilter.class);

        return http.build();
    }
}