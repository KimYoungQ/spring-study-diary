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
                                                "studyDate": "2026-01-15",
                                                "createdAt": "2026-01-15T10:30:00",
                                                "updatedAt": "2026-01-15T10:30:00"
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
                                              "errorMessage": "title: 학습 주제는 필수입니다"
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
                                              "studyDate": "2026-01-15"
                                            }
                                            """
                            )
                    )
            )
            @Valid StudyLogCreateRequest request
    );

    // ========== READ ==========

    @Operation(
            summary = "전체 학습 기록 조회",
            description = """
                    모든 학습 기록을 조회합니다.

                    ### 주의사항
                    - 데이터가 많을 경우 페이징 조회(`GET /api/v1/logs/page`)를 권장합니다
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "title": "Spring Security JWT 인증",
                                                  "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                  "category": "SPRING",
                                                  "categoryIcon": "🌱",
                                                  "understanding": "GOOD",
                                                  "understandingEmoji": "😊",
                                                  "studyTime": 120,
                                                  "studyDate": "2026-01-15",
                                                  "createdAt": "2026-01-15T10:30:00",
                                                  "updatedAt": "2026-01-15T10:30:00"
                                                },
                                                {
                                                  "id": 2,
                                                  "title": "JPA N+1 문제 해결",
                                                  "content": "Fetch Join과 EntityGraph를 사용하여 N+1 문제를 해결했습니다.",
                                                  "category": "JPA",
                                                  "categoryIcon": "🗄️",
                                                  "understanding": "NORMAL",
                                                  "understandingEmoji": "😐",
                                                  "studyTime": 90,
                                                  "studyDate": "2026-01-16",
                                                  "createdAt": "2026-01-16T14:00:00",
                                                  "updatedAt": "2026-01-16T14:00:00"
                                                }
                                              ],
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<StudyLogResponse>>> getAllStudyLogs();

    @Operation(
            summary = "학습 기록 단건 조회",
            description = """
                    ID로 특정 학습 기록을 조회합니다.

                    ### 주의사항
                    - 존재하지 않는 ID 조회 시 404 응답
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
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
                                                "studyDate": "2026-01-15",
                                                "createdAt": "2026-01-15T10:30:00",
                                                "updatedAt": "2026-01-15T10:30:00"
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "학습 기록을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "RESOURCE_NOT_FOUND",
                                              "errorMessage": "StudyLog not found with ID: 999"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<StudyLogResponse>> getStudyLogById(
            @Parameter(description = "조회할 학습 기록 ID", required = true, example = "1")
            @Positive(message = "ID는 양수여야 합니다") Long id
    );

    @Operation(
            summary = "날짜별 학습 기록 조회",
            description = """
                    특정 날짜에 작성된 학습 기록을 조회합니다.

                    ### 날짜 형식
                    - ISO 8601 형식: `yyyy-MM-dd` (예: 2026-01-15)

                    ### 주의사항
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "title": "Spring Security JWT 인증",
                                                  "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                  "category": "SPRING",
                                                  "categoryIcon": "🌱",
                                                  "understanding": "GOOD",
                                                  "understandingEmoji": "😊",
                                                  "studyTime": 120,
                                                  "studyDate": "2026-01-15",
                                                  "createdAt": "2026-01-15T10:30:00",
                                                  "updatedAt": "2026-01-15T10:30:00"
                                                }
                                              ],
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<StudyLogResponse>>> getStudyLogByDate(
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", required = true, example = "2026-01-15")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @Operation(
            summary = "카테고리별 학습 기록 조회",
            description = """
                    특정 카테고리의 학습 기록을 조회합니다.

                    ### 카테고리 목록
                    - JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC

                    ### 주의사항
                    - 대소문자를 구분합니다
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "title": "Spring Security JWT 인증",
                                                  "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                  "category": "SPRING",
                                                  "categoryIcon": "🌱",
                                                  "understanding": "GOOD",
                                                  "understandingEmoji": "😊",
                                                  "studyTime": 120,
                                                  "studyDate": "2026-01-15",
                                                  "createdAt": "2026-01-15T10:30:00",
                                                  "updatedAt": "2026-01-15T10:30:00"
                                                }
                                              ],
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<List<StudyLogResponse>>> getStudyLogsByCategory(
            @Parameter(description = "카테고리명 (JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC)", required = true, example = "SPRING")
            String category
    );

    // ========== PAGING ==========

    @Operation(
            summary = "전체 학습 기록 페이징 조회",
            description = """
                    전체 학습 기록을 페이징하여 조회합니다.

                    ### 파라미터 기본값
                    - **page**: 0 (첫 번째 페이지)
                    - **size**: 10 (페이지당 10건)

                    ### 주의사항
                    - page는 0부터 시작합니다
                    - size는 최소 1 이상이어야 합니다
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "title": "Spring Security JWT 인증",
                                                    "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                    "category": "SPRING",
                                                    "categoryIcon": "🌱",
                                                    "understanding": "GOOD",
                                                    "understandingEmoji": "😊",
                                                    "studyTime": 120,
                                                    "studyDate": "2026-01-15",
                                                    "createdAt": "2026-01-15T10:30:00",
                                                    "updatedAt": "2026-01-15T10:30:00"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 25,
                                                "totalPages": 3,
                                                "first": true,
                                                "last": false,
                                                "hasNext": true,
                                                "hasPrevious": false
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Page<StudyLogResponse>>> getStudyLogsPage(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") int size
    );

    @Operation(
            summary = "카테고리별 학습 기록 페이징 조회",
            description = """
                    특정 카테고리의 학습 기록을 페이징하여 조회합니다.

                    ### 카테고리 목록
                    - JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC

                    ### 파라미터 기본값
                    - **page**: 0 (첫 번째 페이지)
                    - **size**: 10 (페이지당 10건)

                    ### 주의사항
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "title": "Spring Security JWT 인증",
                                                    "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                    "category": "SPRING",
                                                    "categoryIcon": "🌱",
                                                    "understanding": "GOOD",
                                                    "understandingEmoji": "😊",
                                                    "studyTime": 120,
                                                    "studyDate": "2026-01-15",
                                                    "createdAt": "2026-01-15T10:30:00",
                                                    "updatedAt": "2026-01-15T10:30:00"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 5,
                                                "totalPages": 1,
                                                "first": true,
                                                "last": true,
                                                "hasNext": false,
                                                "hasPrevious": false
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Page<StudyLogResponse>>> getStudyLogsByCategoryPage(
            @Parameter(description = "카테고리명 (JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC)", required = true, example = "SPRING")
            String category,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") int size
    );

    @Operation(
            summary = "학습 기록 검색 (페이징)",
            description = """
                    다양한 조건으로 학습 기록을 검색합니다.

                    ### 검색 조건 (모두 선택사항)
                    - **title**: 제목 키워드 (부분 일치)
                    - **category**: 카테고리 (정확히 일치)
                    - **startDate**: 검색 시작 날짜
                    - **endDate**: 검색 종료 날짜

                    ### 파라미터 기본값
                    - **page**: 0 (첫 번째 페이지)
                    - **size**: 10 (페이지당 10건)

                    ### 사용 예시
                    - `GET /api/v1/logs/search?title=Spring`
                    - `GET /api/v1/logs/search?category=SPRING&startDate=2026-01-01&endDate=2026-12-31`
                    - `GET /api/v1/logs/search?title=JWT&category=SPRING&page=0&size=5`

                    ### 주의사항
                    - 모든 조건을 생략하면 전체 조회와 동일합니다
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "title": "Spring Security JWT 인증",
                                                    "content": "JWT 토큰 생성 및 검증 로직 구현 완료",
                                                    "category": "SPRING",
                                                    "categoryIcon": "🌱",
                                                    "understanding": "GOOD",
                                                    "understandingEmoji": "😊",
                                                    "studyTime": 120,
                                                    "studyDate": "2026-01-15",
                                                    "createdAt": "2026-01-15T10:30:00",
                                                    "updatedAt": "2026-01-15T10:30:00"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 1,
                                                "totalPages": 1,
                                                "first": true,
                                                "last": true,
                                                "hasNext": false,
                                                "hasPrevious": false
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Page<StudyLogResponse>>> searchStudyLogs(
            @Parameter(description = "제목 검색 키워드 (부분 일치)", example = "Spring")
            String title,
            @Parameter(description = "카테고리명", example = "SPRING")
            String category,
            @Parameter(description = "검색 시작 날짜 (yyyy-MM-dd)", example = "2026-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "검색 종료 날짜 (yyyy-MM-dd)", example = "2026-12-31")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") int size
    );

    // ========== UPDATE ==========

    @Operation(
            summary = "학습 기록 수정",
            description = """
                    기존 학습 기록을 수정합니다.

                    ### 검증 규칙
                    - **title**: 필수, 1-100자
                    - **content**: 필수, 1-1000자
                    - **category**: 필수, JAVA/SPRING/JPA/DATABASE/ALGORITHM/CS/NETWORK/GIT/ETC 중 선택
                    - **understanding**: 필수, VERY_GOOD/GOOD/NORMAL/BAD/VERY_BAD 중 선택
                    - **studyTime**: 필수, 1-1440분 (1분~24시간)
                    - **studyDate**: 선택, 생략 시 기존 날짜 유지

                    ### 주의사항
                    - 존재하지 않는 ID 수정 시 404 응답
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "id": 1,
                                                "title": "Spring Security JWT 인증 (수정)",
                                                "content": "JWT 토큰 생성, 검증, 리프레시 로직까지 구현 완료",
                                                "category": "SPRING",
                                                "categoryIcon": "🌱",
                                                "understanding": "VERY_GOOD",
                                                "understandingEmoji": "🤩",
                                                "studyTime": 180,
                                                "studyDate": "2026-01-15",
                                                "createdAt": "2026-01-15T10:30:00",
                                                "updatedAt": "2026-01-15T15:00:00"
                                              },
                                              "errorCode": null,
                                              "errorMessage": null`
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
                                              "errorMessage": "title: 학습 주제는 필수입니다"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "학습 기록을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "RESOURCE_NOT_FOUND",
                                              "errorMessage": "StudyLog not found with ID: 999"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<StudyLogResponse>> updateStudyLog(
            @Parameter(description = "수정할 학습 기록 ID", required = true, example = "1")
            @Positive(message = "ID는 양수여야 합니다") Long id,
            @RequestBody(
                    description = "학습 기록 수정 요청 데이터",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StudyLogUpdateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "Spring Security JWT 인증 (수정)",
                                              "content": "JWT 토큰 생성, 검증, 리프레시 로직까지 구현 완료",
                                              "category": "SPRING",
                                              "understanding": "VERY_GOOD",
                                              "studyTime": 180,
                                              "studyDate": "2026-01-15"
                                            }
                                            """
                            )
                    )
            )
            @Valid StudyLogUpdateRequest request
    );

    // ========== DELETE ==========

    @Operation(
            summary = "학습 기록 삭제",
            description = """
                    학습 기록을 삭제합니다.

                    ### 주의사항
                    - 삭제된 기록은 복구할 수 없습니다
                    - 존재하지 않는 ID 삭제 시 404 응답
                    - JWT 토큰 인증이 필요합니다
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "message": "학습 기록이 삭제되었습니다.",
                                                "deletedId": 1
                                              },
                                              "errorCode": null,
                                              "errorMessage": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "학습 기록을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "errorCode": "RESOURCE_NOT_FOUND",
                                              "errorMessage": "StudyLog not found with ID: 999"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<StudyLogDeleteResponse>> deleteStudyLog(
            @Parameter(description = "삭제할 학습 기록 ID", required = true, example = "1")
            @Positive(message = "ID는 양수여야 합니다") Long id
    );
}
