package com.study.my_spring_study_diary.auth.service;

import com.study.my_spring_study_diary.auth.dao.UserDao;
import com.study.my_spring_study_diary.auth.dto.LoginRequest;
import com.study.my_spring_study_diary.auth.dto.LoginResponse;
import com.study.my_spring_study_diary.auth.dto.SignupRequest;
import com.study.my_spring_study_diary.auth.dto.SignupResponse;
import com.study.my_spring_study_diary.auth.entity.User;
import com.study.my_spring_study_diary.auth.entity.UserRole;
import com.study.my_spring_study_diary.global.Security.jwt.JwtTokenProvider;
import com.study.my_spring_study_diary.study_log.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // Given
        // TODO: userRepository.existsByEmail()이 false를 반환하도록 설정
        // TODO: passwordEncoder.encode()가 암호화된 문자열을 반환하도록 설정
        // TODO: userRepository.save()가 ID가 부여된 User를 반환하도록 설정
        String email = "example@test.com";
        String username = "testUser";
        String password = "test1234!";

        SignupRequest request = SignupRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        when(userDao.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User newUser = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .username(username)
                .role(UserRole.USER)
                .enabled(true)
                .build();

        when(userDao.save(any(User.class))).thenReturn(newUser);

        // When
        SignupResponse response = authService.signup(request);

        // Then
        // TODO: registeredUser의 이름, 이메일을 검증하세요
        // TODO: emailService.sendWelcomeEmail()이 호출되었는지 검증하세요
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(newUser.getEmail()).isEqualTo(email);

        verify(userDao).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 username으로 회원가입 시 예외 발생")
    void signup_duplicateUsername() {
        // Given
        // TODO: userRepository.existsByUsername()이 true를 반환하도록 설정
        String username = "existedName";
        String password = "test1234!";
        String email = "test@example.com";

        SignupRequest request = SignupRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        User existedUser = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        when(userDao.findByUsername(username)).thenReturn(Optional.of(existedUser));

        // When & Then
        // TODO: signup 호출 시 DuplicateUsernameException이 발생하는지 검증
        // TODO: userRepository.save()가 호출되지 않았는지 검증
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists with " + username);

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰 반환")
    void login_success() {
        // Given
        String username = "testuser";
        String password = "encoded-password";

        User user = User.builder()
                .id(1L)
                .username(username)
                .password(password)
                .role(UserRole.USER)
                .build();

        // TODO: userRepository.findByUsername()이 user를 반환하도록 설정
        // TODO: passwordEncoder.matches()가 true를 반환하도록 설정
        // TODO: jwtTokenProvider의 메서드들이 토큰을 반환하도록 설정

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));

        //when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        when(jwtTokenProvider.generateAccessToken(username,"ROLE_USER")).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(username)).thenReturn("refreshToken");;
        when(jwtTokenProvider.getExpirationFromToken("refreshToken"))
                .thenReturn(new Date(System.currentTimeMillis() + 604800000));

        // When
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = authService.login(request);

        // Then
        // TODO: response에 accessToken이 있는지 검증
        // TODO: tokenType이 "Bearer"인지 검증
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }
}