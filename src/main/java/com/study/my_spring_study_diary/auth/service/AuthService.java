package com.study.my_spring_study_diary.auth.service;

import com.study.my_spring_study_diary.auth.dao.UserDao;
import com.study.my_spring_study_diary.auth.dto.LoginRequest;
import com.study.my_spring_study_diary.auth.dto.LoginResponse;
import com.study.my_spring_study_diary.auth.dto.SignupRequest;
import com.study.my_spring_study_diary.auth.dto.SignupResponse;
import com.study.my_spring_study_diary.auth.entity.User;
import com.study.my_spring_study_diary.auth.entity.UserRole;
import com.study.my_spring_study_diary.auth.exception.AuthException;
import com.study.my_spring_study_diary.global.Security.jwt.JwtTokenProvider;
import com.study.my_spring_study_diary.study_log.exception.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    /**
     * User login
     * POST /api/auth/login
     */
    public LoginResponse login(@Valid LoginRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // 1. 미인증 Authentication 토큰 생성 → Spring Security에 인증 요청
            // Client가 입력한 username과 password를 사용하여
            // 인증(입력 값이 DB에 저장되어 있는 값과 일치하는지를 확인하는 역할)을 수행한다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 2. 인증 성공 → DB에서 사용자 정보 조회
            User user = userDao.findByUsername(request.getUsername())
                    .orElseGet(() -> userDao.findByEmail(request.getUsername())
                            .orElseThrow(() -> new AuthException("User not found")));

            // 3. 권한 정보 추출
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            // 4. Access Token + Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), roles);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

            // 5. Refresh Token을 DB에 저장
            Date refreshTokenExpiry = jwtTokenProvider.getExpirationFromToken(refreshToken);
            userDao.saveRefreshToken(user.getId(), refreshToken, new Timestamp(refreshTokenExpiry.getTime()));

            log.info("Login successful for user: {}", user.getUsername());

            return LoginResponse.of(
                    accessToken,
                    refreshToken,
                    accessTokenValidity,
                    user.getUsername(),
                    user.getEmail()
            );

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    /**
     * User signup/registration
     * POST /api/auth/signup
     */
    @Transactional
    public SignupResponse signup(SignupRequest request){
        log.info("Signup attempt for username: {}. email: {}", request.getUsername(), request.getEmail());

        // 1. 아이디 중복 검사
        if(userDao.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Study log not found with ID: {}", request.getUsername());
            throw new DuplicateResourceException("Username", request.getUsername());
        }


        // 2. 이메일 중복 검사
        if(userDao.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Study log not found with ID: {}", request.getUsername());
            throw new DuplicateResourceException("Email", request.getEmail());
        }

        // 3. 사용자 엔티티 생성 및 저장
        // Password를 평문으로 저장을 하면 안된다.
        // BCryptPasswordEncoder를 사용하여 암호화된 비밀번호를 저장한다.
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        User savedUser = userDao.save(newUser);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return SignupResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();

    }
}
