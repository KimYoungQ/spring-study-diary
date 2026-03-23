package com.study.my_spring_study_diary.study_log.controller;

import com.study.my_spring_study_diary.global.common.ApiResponse;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.service.StudyLogJpaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StudyLogJpaController {

    private final StudyLogJpaService studyLogJpaService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudyLogResponse>> createStudyLog(
            @Valid @RequestBody StudyLogCreateRequest request) {

        log.info("학습 일지 생성 요청: title={}, category={}, studyTime={}분",
                request.getTitle(), request.getCategory(), request.getStudyTime());
        log.debug("학습 일지 상세 내용: understanding={}, studyDate={}, contentLength={}",
                request.getUnderstanding(), request.getStudyDate(),
                request.getContent() != null ? request.getContent().length() : 0);

        try {
            // Service 호출하여 학습 일지 생성
            StudyLogResponse response = studyLogJpaService.create(request);

            log.info("학습 일지 생성 성공: id={}, title={}", response.getId(), response.getTitle());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("학습 일지 생성 실패: title={}", request.getTitle(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyLogResponse>>>  getAllStudyLogs() {

        return ResponseEntity.ok(ApiResponse.success(studyLogJpaService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudyLogResponse>> getStudyLogById(
            @PathVariable @Positive(message = "ID는 양수여야 합니다") Long id) {

        log.info("학습 일지 단건 조회 요청: id={}", id);
        try {
            StudyLogResponse response = studyLogJpaService.findById(id);
            log.debug("학습 일지 조회 성공: id={}, title={}", id, response.getTitle());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("학습 일지 조회 실패: id={}", id, e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudyLogResponse>>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return  ResponseEntity.ok(ApiResponse.success(studyLogJpaService.search(title, category, startDate, endDate)));
    }
}
