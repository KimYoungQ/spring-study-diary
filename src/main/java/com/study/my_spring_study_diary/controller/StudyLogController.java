package com.study.my_spring_study_diary.controller;

import com.study.my_spring_study_diary.global.common.ApiResponse;
import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.dto.response.StudyLogDeleteResponse;
import com.study.my_spring_study_diary.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.service.StudyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 학습 일지 컨트롤러
 *
 * @RestController 어노테이션 설명:
 * - @Controller + @ResponseBody 의 조합
 * - 이 클래스의 모든 메서드 반환값을 JSON으로 변환하여 응답
 * - REST API 개발 시 사용
 * @RequestMapping 어노테이션 설명:
 * - 이 컨트롤러의 기본 URL 경로를 설정
 * - 모든 메서드의 URL 앞에 "/api/v1/logs"가 붙음
 */

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class StudyLogController {

    private final StudyLogService studyLogService;

    /**
     * 학습 일지 생성 (CREATE)
     *
     * @PostMapping: POST 요청을 처리
     * @RequestBody: HTTP Body의 JSON을 객체로 변환
     * <p>
     * POST /api/v1/logs
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudyLogResponse>>  createStudyLog(@RequestBody StudyLogCreateRequest request) {

        //Service 호출하여 학습 일지 생성
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(studyLogService.createStudyLog(request)));
    }

    /**
     * 모든 학습 일지 조회 (READ - All)
     *
     * @GetMapping: GET 요청을 처리
     * <p>
     * GET /api/v1/logs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyLogResponse>>>  getAllStudyLogs() {

        // Service 호출하여 모든 학습 일지 조회
        return ResponseEntity.ok(ApiResponse.success(studyLogService.getAllStudyLogs()));
    }

    /**
     * 특정 학습 일지 조회 (READ - Single)
     *
     * @GetMapping("/{id}"): GET 요청을 처리 (경로 변수 포함)
     * @PathVariable: URL 경로의 {id} 값을 매개변수로 받음
     * <p>
     * GET /api/v1/logs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudyLogResponse>> getStudyLogById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.getStudyLogById(id)));
    }

    /**
     * 날짜별 학습 일지 조회 (READ - By Date)
     *
     * @GetMapping("/date/{date}"): GET 요청을 처리 (날짜 경로 변수 포함)
     * @PathVariable: URL 경로의 {date} 값을 매개변수로 받음
     * <p>
     * GET /api/v1/logs/date/{date}
     * 예시: GET /api/v1/logs/date/2025-01-15
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<StudyLogResponse>>> getStudyLogByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.getStudyLogsByDate(date)));
    }

    /**
     * 카테고리별 학습 일지 조회 (READ - By Category)
     *
     * @GetMapping("/category/{category}"): GET 요청을 처리 (카테고리 경로 변수 포함)
     * @PathVariable: URL 경로의 {category} 값을 매개변수로 받음
     * <p>
     * GET /api/v1/logs/category/{category}
     * 예시: GET /api/v1/logs/category/SPRING
     * GET /api/v1/logs/category/JAVA
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<StudyLogResponse>>> getStudyLogsByCategory(@PathVariable String category) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.getStudyLogsByCategory(category)));
    }

    // ========== PAGING ==========

    /**
     * 전체 학습 일지 페이징 조회
     * <p>
     * GET /api/v1/logs/page?page=0&size=10
     * GET /api/v1/logs/page (기본값: page=0, size=10)
     *
     * @param page 페이지 번호 (0-based, 기본값: 0)
     * @param size 페이지 크기 (기본값: 10, 최대: 100)
     * @return 페이징된 학습 일지
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<StudyLogResponse>>> getStudyLogsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.getStudyLogsWithPaging(page, size)));
    }

    /**
     * 카테고리별 학습 일지 페이징 조회
     *
     * GET /api/v1/logs/category/{category}/page?page=0&size=10
     *
     * @param category 카테고리
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징된 학습 일지
     */
    @GetMapping("/category/{category}/page")
    public ResponseEntity<ApiResponse<Page<StudyLogResponse>>> getStudyLogsByCategoryPage(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.getStudyLogsByCategoryWithPaging(category, page, size)));
    }

    /**
     * 검색 + 페이징 조회
     *
     * GET /api/v1/logs/search?title=Spring&category=SPRING
     *     &startDate=2026-01-01&endDate=2026-12-31
     *     &page=0&size=10
     *
     * @param title 제목 키워드 (선택)
     * @param category 카테고리 (선택)
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징된 검색 결과
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<StudyLogResponse>>> searchStudyLogs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.searchStudyLogsWithPaging(
                title, category, startDate, endDate, page, size)));
    }

    // ========== UPDATE ==========

    /**
     * 학습 일지 수정
     * PUT /api/v1/logs/{id}
     *
     * @PutMapping: PUT 요청을 처리하는 어노테이션
     *              리소스의 전체 또는 일부를 수정할 때 사용
     *
     * @PathVariable: URL의 {id} 부분을 파라미터로 받음
     * @RequestBody: HTTP Body의 JSON을 객체로 변환
     */
    @PutMapping("/{id}")

    public ResponseEntity<ApiResponse<StudyLogResponse>> updateStudyLog(
            @PathVariable Long id,
            @RequestBody StudyLogUpdateRequest request) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.updateStudyLog(id, request)));
    }

    // ========== DELETE ==========

    /**
     * 학습 일지 삭제 API
     * <p>
     * DELETE /api/v1/logs/{id}
     *
     * @param id 삭제할 학습 일지 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<StudyLogDeleteResponse>> deleteStudyLog(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(studyLogService.deleteStudyLog(id)));
    }
}
