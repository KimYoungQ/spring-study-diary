package com.study.my_spring_study_diary.study_log.controller;

import com.study.my_spring_study_diary.global.common.ApiResponse;
import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogDeleteResponse;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Study Log API Documentation Interface
 * 학습 기록 관련 API 문서화를 위한 인터페이스
 */
@Tag(name = "학습 기록", description = "학습 기록 CRUD API - JWT 인증 필요")
public interface StudyLogControllerApi {

    @Operation(
            summary = "학습 기록 생성",
            description = """
                    새로운 학습 기록을 생성합니다.

                    ### 검증 규칙
                    - **title**: 필수, 1-100자
                    - **content**: 필수, 1-1000자
                    - **category**: 필수, JAVA/SPRING/JPA/DATABASE/ALGORITHM/CS/NETWORK/GIT/ETC 중 선택
                    - **understanding**: 필수, VERY_GOOD/GOOD/NORMAL/BAD/VERY_BAD 중 선택
                    - **studyTime**: 필수, 1-1440분 (1분~24시간)
                    - **studyDate**: 선택, 생략 시 현재 날짜

                    ### 주의사항
                    - 중복된 제목도 허용됩니다
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "id": 1,
                                                "title": "Spring Security JWT 인증",
                                                "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                "category": "SPRING",
                                                "categoryIcon": "🌱",
                                                "understanding": "GOOD",
                                                "understandingEmoji": "😊",
                                                "studyTime": 120,
                                                "studyDate": "2024-01-15",
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-15T10:30:00"
                                              },
                                              "error": null
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
                                              "error": {
                                                "code": "VALIDATION_ERROR",
                                                "message": "학습 주제는 필수입니다"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 토큰 없음 또는 만료",
                    content = @Content(
                            mediaType =  "application/json",
                            examples = @ExampleObject(
                                    """
                                    {
                                      "success": false,
                                        "data": null,
                                        "error": {
                                          "code": "TOKEN_EXPIRED",
                                          "message": "Token has expired"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<StudyLogResponse>> createStudyLog(
            @RequestBody(
                    description = "학습 기록 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StudyLogCreateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "JPA N+1 문제 해결",
                                              "content": "Fetch Join과 EntityGraph를 사용하여 N+1 문제를 해결했습니다.",
                                              "category": "JPA",
                                              "understanding": "GOOD",
                                              "studyTime": 90,
                                              "studyDate": "2024-01-15"
                                            }
                                            """
                            )
                    )
            )
            @Valid StudyLogCreateRequest request
    );
}