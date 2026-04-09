package com.study.my_spring_study_diary.auth.service;

import com.study.my_spring_study_diary.auth.dao.RefreshTokenDao;
import com.study.my_spring_study_diary.auth.dao.UserDao;
import com.study.my_spring_study_diary.auth.dto.request.LoginRequest;
import com.study.my_spring_study_diary.auth.dto.request.SignupRequest;
import com.study.my_spring_study_diary.auth.dto.request.TokenRefreshRequest;
import com.study.my_spring_study_diary.auth.dto.response.LoginResponse;
import com.study.my_spring_study_diary.auth.dto.response.SignupResponse;
import com.study.my_spring_study_diary.auth.dto.response.TokenRefreshResponse;
import com.study.my_spring_study_diary.auth.exception.InvalidTokenException;
import com.study.my_spring_study_diary.auth.entity.User;
import com.study.my_spring_study_diary.auth.entity.UserRole;
import com.study.my_spring_study_diary.event.auth.UserRegisteredEvent;
import com.study.my_spring_study_diary.global.Security.jwt.JwtTokenProvider;
import com.study.my_spring_study_diary.study_log.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenDao refreshTokenDao;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenValidity", 3600L);
        ReflectionTestUtils.setField(authService, "refreshTokenValidity", 604800L);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("로그인 성공")
        void login_Success() {
            // Given
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    testUser.getUsername(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userDao.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(jwtTokenProvider.generateAccessToken("testuser", "ROLE_USER"))
                    .thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken("testuser"))
                    .thenReturn("refresh-token");
            when(jwtTokenProvider.getExpirationFromToken("refresh-token"))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000));

            // When
            LoginResponse response = authService.login(loginRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getExpiresIn()).isEqualTo(3600L);
            assertThat(response.getTokenType()).isEqualTo("Bearer");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userDao).findByUsername("testuser");
            verify(jwtTokenProvider).generateAccessToken("testuser", "ROLE_USER");
            verify(jwtTokenProvider).generateRefreshToken("testuser");
            verify(userDao).saveRefreshToken(eq(1L), eq("refresh-token"), any(Timestamp.class));
        }

        @Test
        @DisplayName("이메일로 로그인 성공")
        void login_SuccessWithEmail() {
            // Given
            LoginRequest emailLoginRequest = LoginRequest.builder()
                    .username("test@example.com")
                    .password("password123")
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "test@example.com",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userDao.findByUsername("test@example.com")).thenReturn(Optional.empty());
            when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(jwtTokenProvider.generateAccessToken(anyString(), anyString()))
                    .thenReturn("access-token");
            when(jwtTokenProvider.generateRefreshToken(anyString()))
                    .thenReturn("refresh-token");
            when(jwtTokenProvider.getExpirationFromToken("refresh-token"))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000));

            // When
            LoginResponse response = authService.login(emailLoginRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getEmail()).isEqualTo("test@example.com");

            verify(userDao).findByUsername("test@example.com");
            verify(userDao).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 실패")
        void login_FailWithInvalidCredentials() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // When & Then
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid username or password");

            verify(userDao, never()).saveRefreshToken(anyLong(), anyString(), any(Timestamp.class));
        }
    }

    @Nested
    @DisplayName("회원가입")
    class Signup {

        private SignupRequest signupRequest;

        @BeforeEach
        void setUpSignupRequest() {
            signupRequest = SignupRequest.builder()
                    .username("newuser")
                    .password("Password1!")
                    .email("newuser@example.com")
                    .build();
        }

        @Test
        @DisplayName("회원가입 성공")
        void signup_Success() {
            // Given
            User savedUser = User.builder()
                    .id(2L)
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("encodedPassword")
                    .role(UserRole.USER)
                    .build();

            when(userDao.findByUsername("newuser")).thenReturn(Optional.empty());
            when(userDao.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("Password1!")).thenReturn("encodedPassword");
            when(userDao.save(any(User.class))).thenReturn(savedUser);

            // When
            SignupResponse response = authService.signup(signupRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(2L);
            assertThat(response.getUsername()).isEqualTo("newuser");
            assertThat(response.getEmail()).isEqualTo("newuser@example.com");

            verify(userDao).findByUsername("newuser");
            verify(userDao).findByEmail("newuser@example.com");
            verify(passwordEncoder).encode("Password1!");
            verify(userDao).save(any(User.class));
        }

        @Test
        @DisplayName("회원가입 성공 시 이벤트 발행 확인")
        void signup_PublishesUserRegisteredEvent() {
            // Given
            User savedUser = User.builder()
                    .id(2L)
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("encodedPassword")
                    .role(UserRole.USER)
                    .build();

            when(userDao.findByUsername("newuser")).thenReturn(Optional.empty());
            when(userDao.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("Password1!")).thenReturn("encodedPassword");
            when(userDao.save(any(User.class))).thenReturn(savedUser);

            // When
            authService.signup(signupRequest);

            // Then
            verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
        }

        @Test
        @DisplayName("아이디 중복 시 DuplicateResourceException")
        void signup_FailWhenUsernameDuplicate() {
            // Given
            when(userDao.findByUsername("newuser")).thenReturn(Optional.of(testUser));

            // When & Then
            assertThatThrownBy(() -> authService.signup(signupRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(userDao).findByUsername("newuser");
            verify(userDao, never()).save(any(User.class));
        }

        @Test
        @DisplayName("이메일 중복 시 DuplicateResourceException")
        void signup_FailWhenEmailDuplicate() {
            // Given
            when(userDao.findByUsername("newuser")).thenReturn(Optional.empty());
            when(userDao.findByEmail("newuser@example.com")).thenReturn(Optional.of(testUser));

            // When & Then
            assertThatThrownBy(() -> authService.signup(signupRequest))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(userDao).findByUsername("newuser");
            verify(userDao).findByEmail("newuser@example.com");
            verify(userDao, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class Refresh {

        private TokenRefreshRequest refreshRequest;

        @BeforeEach
        void setUpRefreshRequest() {
            refreshRequest = TokenRefreshRequest.builder()
                    .refreshToken("valid-refresh-token")
                    .build();
        }

        @Test
        @DisplayName("토큰 갱신 성공")
        void refresh_Success() {
            // Given
            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken("valid-refresh-token")).thenReturn(true);
            when(userDao.findByRefreshToken("valid-refresh-token")).thenReturn(Optional.of(testUser));
            when(jwtTokenProvider.generateAccessToken(1L, "testuser", "ROLE_USER"))
                    .thenReturn("new-access-token");
            when(jwtTokenProvider.generateRefreshToken("testuser"))
                    .thenReturn("new-refresh-token");
            when(jwtTokenProvider.getExpirationFromToken("new-refresh-token"))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000));

            // When
            TokenRefreshResponse response = authService.refresh(refreshRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

            verify(jwtTokenProvider).validateToken("valid-refresh-token");
            verify(jwtTokenProvider).isRefreshToken("valid-refresh-token");
            verify(userDao).findByRefreshToken("valid-refresh-token");
            verify(refreshTokenDao).saveRefreshToken(eq(1L), eq("new-refresh-token"), any(Timestamp.class));
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 갱신 실패")
        void refresh_FailWhenTokenInvalid() {
            // Given
            when(jwtTokenProvider.validateToken("valid-refresh-token"))
                    .thenThrow(new InvalidTokenException("Invalid refresh token"));

            // When & Then
            assertThatThrownBy(() -> authService.refresh(refreshRequest))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Invalid refresh token");

            verify(userDao, never()).findByRefreshToken(anyString());
        }

        @Test
        @DisplayName("리프레시 토큰이 아닌 경우 갱신 실패")
        void refresh_FailWhenNotRefreshToken() {
            // Given
            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken("valid-refresh-token")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.refresh(refreshRequest))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token is not a refresh token");

            verify(userDao, never()).findByRefreshToken(anyString());
        }

        @Test
        @DisplayName("DB에 리프레시 토큰이 없는 경우 갱신 실패")
        void refresh_FailWhenRefreshTokenNotFoundInDB() {
            // Given
            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken("valid-refresh-token")).thenReturn(true);
            when(userDao.findByRefreshToken("valid-refresh-token")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> authService.refresh(refreshRequest))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Refresh token not found or expired");
        }

        @Test
        @DisplayName("토큰 갱신 null role 처리")
        void refresh_DefaultsToUserRoleWhenRoleIsNull() {
            // Given
            User userWithoutRole = User.builder()
                    .id(3L)
                    .username("noroleuser")
                    .email("norole@example.com")
                    .password("encodedPassword")
                    .role(null)
                    .build();

            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken("valid-refresh-token")).thenReturn(true);
            when(userDao.findByRefreshToken("valid-refresh-token")).thenReturn(Optional.of(userWithoutRole));
            when(jwtTokenProvider.generateAccessToken(3L, "noroleuser", "ROLE_USER"))
                    .thenReturn("new-access-token");
            when(jwtTokenProvider.generateRefreshToken("noroleuser"))
                    .thenReturn("new-refresh-token");
            when(jwtTokenProvider.getExpirationFromToken("new-refresh-token"))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000));

            // When
            TokenRefreshResponse response = authService.refresh(refreshRequest);

            // Then
            assertThat(response).isNotNull();
            verify(jwtTokenProvider).generateAccessToken(3L, "noroleuser", "ROLE_USER");
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("로그아웃 성공 - 리프레시 토큰 삭제")
        void logout_Success() {
            // Given
            String refreshToken = "refresh-token-to-delete";

            // When
            authService.logout(refreshToken);

            // Then
            verify(refreshTokenDao).deleteRefreshToken(refreshToken);
        }
    }
}
