package com.study.my_spring_study_diary.auth.controller;

import com.study.my_spring_study_diary.auth.dto.request.LoginRequest;
import com.study.my_spring_study_diary.auth.dto.request.SignupRequest;
import com.study.my_spring_study_diary.auth.dto.request.TokenRefreshRequest;
import com.study.my_spring_study_diary.auth.dto.response.LoginResponse;
import com.study.my_spring_study_diary.auth.dto.response.SignupResponse;
import com.study.my_spring_study_diary.auth.dto.response.TokenRefreshResponse;
import com.study.my_spring_study_diary.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

/**
 * Auth API Documentation Interface
 * 인증 관련 API 문서화를 위한 인터페이스
 */
@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃, 토큰 갱신 API")
public interface AuthControllerApi {

    @Operation(
            summary = "로그인",
            description = """
                    사용자 로그인을 처리하고 JWT 토큰을 발급합니다.

                    ### 검증 규칙
                    - **username**: 필수
                    - **password**: 필수

                    ### 응답 토큰
                    - **access_token**: API 요청 시 사용하는 토큰
                    - **refresh_token**: access_token 만료 시 갱신에 사용
                    - **expires_in**: access_token 만료 시간 (초)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "access_token": "eyJhbGciOiJIUzI1NiJ9...",
                                                "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
                                                "token_type": "Bearer",
                                                "expires_in": 3600,
                                                "username": "testuser",
                                                "email": "test@example.com"
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "VALIDATION_ERROR",
                                              "errorMessage": "아이디는 필수입니다"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 아이디 또는 비밀번호 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "INTERNAL_ERROR",
                                              "errorMessage": "Invalid username or password"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody(
                    description = "로그인 요청 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "username": "testuser",
                                              "password": "Password1!"
                                            }
                                            """
                            )
                    )
            )
            @Valid LoginRequest request
    );

    @Operation(
            summary = "회원가입",
            description = """
                    새로운 사용자를 등록합니다.

                    ### 검증 규칙
                    - **username**: 필수, 4-20자, 영문/숫자/언더스코어만 허용
                    - **password**: 필수, 최소 8자, 영문/숫자/특수문자 포함
                    - **email**: 필수, 올바른 이메일 형식

                    ### 주의사항
                    - 이미 존재하는 username 또는 email로 가입 시 409 응답
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "id": 1,
                                                "username": "testuser",
                                                "email": "test@example.com"
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "VALIDATION_ERROR",
                                              "errorMessage": "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "중복된 아이디 또는 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "DUPLICATE_RESOURCE",
                                              "errorMessage": "Username already exists with testuser"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody(
                    description = "회원가입 요청 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "username": "testuser",
                                              "password": "Password1!",
                                              "email": "test@example.com"
                                            }
                                            """
                            )
                    )
            )
            @Valid SignupRequest request
    );

    @Operation(
            summary = "로그아웃",
            description = """
                    사용자 로그아웃을 처리합니다.

                    ### 동작
                    - Refresh Token을 무효화합니다
                    - 이후 해당 Refresh Token으로 토큰 갱신이 불가합니다

                    ### 주의사항
                    - 유효한 Refresh Token이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": "Logged out successfully",
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "VALIDATION_ERROR",
                                              "errorMessage": "Refresh token is required"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<String>> logout(
            @RequestBody(
                    description = "로그아웃 요청 데이터 (Refresh Token)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TokenRefreshRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            )
            @Valid TokenRefreshRequest request
    );

    @Operation(
            summary = "토큰 갱신",
            description = """
                    Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.

                    ### 동작
                    - 유효한 Refresh Token으로 새로운 Access Token과 Refresh Token을 발급
                    - 기존 Refresh Token은 무효화됩니다 (Refresh Token Rotation)

                    ### 주의사항
                    - 만료되거나 무효화된 Refresh Token으로는 갱신 불가
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "VALIDATION_ERROR",
                                              "errorMessage": "Refresh token is required"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - Refresh Token 만료 또는 무효",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "INTERNAL_ERROR",
                                              "errorMessage": "Invalid refresh token"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @RequestBody(
                    description = "토큰 갱신 요청 데이터 (Refresh Token)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TokenRefreshRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            )
            @Valid TokenRefreshRequest request
    );
}
