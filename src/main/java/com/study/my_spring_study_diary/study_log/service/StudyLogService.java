package com.study.my_spring_study_diary.study_log.service;

import com.study.my_spring_study_diary.event.study.StudyLogCreatedEvent;
import com.study.my_spring_study_diary.study_log.exception.InvalidPageRequestException;
import com.study.my_spring_study_diary.study_log.exception.ResourceNotFoundException;
import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.study_log.dao.StudyLogDao;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogDeleteResponse;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.global.mapper.StudyLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 학습 일지 서비스
 *
 * @Service 어노테이션 설명:
 * - 이 클래스를 Spring Bean으로 등록합니다
 * - 비즈니스 로직을 담당하는 서비스 계층임을 명시합니다
 * - @Component와 기능적으로 동일하지만, 역할을 명확히 표현합니다
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyLogService {

    // 의존성 주입: Repository를 주입받음
    private final StudyLogDao studyLogDao;
    private final StudyLogMapper studyLogMapper;
    private final ApplicationEventPublisher eventPublisher;

    // 페이징 관련 상수
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 학습 일지 생성
     * @param request 생성 요청 DTO
     * @return 생성된 학습 일지 응답 DTO
     */
    @Transactional
    public StudyLogResponse createStudyLog(StudyLogCreateRequest request) {
        log.info("Creating study log with title: {}", request.getTitle());

        // 2. DTO → Entity 변환 (Using Builder pattern with Lombok)
        // Using MapStruct로 변경
        StudyLog studyLog = studyLogMapper.toEntity(request);

        // 3. 저장 (DAO 사용)
        StudyLog savedStudyLog = studyLogDao.save(studyLog);
        log.info("Successfully created study log with ID: {}", savedStudyLog.getId());

        // 이벤트 발행 추가
        eventPublisher.publishEvent(StudyLogCreatedEvent.from(savedStudyLog));
        log.info("StudyLogCreatedEvent 발행 완료 - ID: {}", savedStudyLog.getId());

        // 4. Entity → Response DTO 변환 후 반환
        return studyLogMapper.toResponse(savedStudyLog);
    }

    /**
     * 전체 학습 일지 목록 조회
     */
    public List<StudyLogResponse> getAllStudyLogs() {
        List<StudyLog> studyLogs = studyLogDao.findAll();

        return studyLogMapper.toResponseList(studyLogs);
    }


    /**
     * ID로 학습 일지 단건 조회
     */
    public StudyLogResponse getStudyLogById(Long id) {

        // 1. DAO에서 ID로 조회 (Optional 반환)
        Optional<StudyLog> studyLogOpt = studyLogDao.findById(id);

        // 2. 존재하지 않으면 예외 처리
        StudyLog studyLog = studyLogOpt.orElseThrow(() -> {
            log.warn("Study log not found with ID: {}", id);
            return new ResourceNotFoundException("Study Log", id);
        });

        // 3. Entity → Response DTO 변환 후 반환 (Using MapStruct)
        return studyLogMapper.toResponse(studyLog);
    }

    /**
     * 날짜별 학습 일지 조회
     */
    public List<StudyLogResponse> getStudyLogsByDate(LocalDate date) {

        List<StudyLog> studyLogs = studyLogDao.findByStudyDate(date);

        return studyLogMapper.toResponseList(studyLogs);
    }

    /**
     * 카테고리 학습 일지 조회
     */
    public List<StudyLogResponse> getStudyLogsByCategory(String categoryString) {

        List<StudyLog> studyLogs = studyLogDao.findByCategory(categoryString);

        // 3. Entity 리스트 → Response DTO 리스트 변환
        return studyLogMapper.toResponseList(studyLogs);
    }

    // ========== PAGING ==========

    /**
     * 페이징 처리된 학습 일지 목록 조회
     */
    public Page<StudyLogResponse> getStudyLogsWithPaging(int page, int size) {

        // 파라미터 유효성 검증
        size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);  // 1~100 범위

        Page<StudyLog> studyLogPage = studyLogDao.findAllWithPaging(page, size);

        // 요청 페이지 범위 검증
        if (page < 0 || page >= studyLogPage.getTotalPages()) {
            throw new InvalidPageRequestException(page, studyLogPage.getTotalPages());
        }

        //Entity를 Response DTO로 변환
        List<StudyLogResponse> content = studyLogPage.getContent().stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());

        // 페이징 정보를 유지하면서 DTO로 변환
        return new Page<>(content, page, size, studyLogPage.getTotalElements());
    }

    /**
     * 카테고리별 페이징 조회
     */
    public Page<StudyLogResponse> getStudyLogsByCategoryWithPaging(String categoryStr, int page, int size) {

        // 파라미터 유효성 검증
        size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);  // 1~100 범위

        // 카테고리 유효성 검증
        if (categoryStr == null || categoryStr.isBlank()) {
            return new Page<>(List.of(), page, size, 0);
        }

        Page<StudyLog> studyLogPage = studyLogDao.findByCategoryWithPaging(categoryStr.toUpperCase(), page, size);

        // 요청 페이지 범위 검증
        if (page < 0 || page >= studyLogPage.getTotalPages()) {
            throw new InvalidPageRequestException(page, studyLogPage.getTotalPages());
        }

        List<StudyLogResponse> content = studyLogPage.getContent().stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());

        return new Page<>(content, page, size, studyLogPage.getTotalElements());
    }

    /**
     * 검색 + 페이징 조회
     * @param titleKeyword 제목 키워드
     * @param categoryStr 카테고리 문자열
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징된 학습 일지 응답
     */
    public Page<StudyLogResponse> searchStudyLogsWithPaging(
            String titleKeyword,
            String categoryStr,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);

        Page<StudyLog> studyLogPage = studyLogDao.searchWithPaging(
                titleKeyword, categoryStr, startDate, endDate, page, size);

        // 요청 페이지 범위 검증
        if (page < 0 || page >= studyLogPage.getTotalPages()) {
            throw new InvalidPageRequestException(page, studyLogPage.getTotalPages());
        }

        List<StudyLogResponse> content = studyLogPage.getContent().stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());

        return new Page<>(content, page, size, studyLogPage.getTotalElements());
    }

    // ========== UPDATE ==========

    /**
     * 학습 일지 수정
     *
     * @param id      수정할 학습 일지 ID
     * @param request 수정 요청 데이터
     * @return 수정된 학습 일지 응답
     */
    public StudyLogResponse updateStudyLog(Long id, StudyLogUpdateRequest request) {

        // 1. 기존 학습 일지 조회
        StudyLog studyLog = studyLogDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study Log", id));

        // 2. 수정할 내용이 있는지 확인
        if (request.hasNoUpdates()) {
            throw new IllegalArgumentException("수정할 내용이 없습니다.");
        }

        // 4. 업데이트
        studyLogMapper.partialUpdate(request, studyLog);

        // 5. 저장 및 응답 반환
        StudyLog updatedStudyLog = studyLogDao.update(studyLog);
        return studyLogMapper.toResponse(updatedStudyLog);
    }

    /**
     * 학습 일지를 삭제합니다.
     *
     * @param id 삭제할 학습 일지 ID
     * @return 삭제 결과 응답
     * @throws ResourceNotFoundException 해당 ID의 학습 일지가validationStudyLogById 없는 경우
     */
    public StudyLogDeleteResponse deleteStudyLog(Long id) {

        studyLogDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study Log", id));

        //2. 삭제 수행
        studyLogDao.deleteById(id);

        // 3. 삭제 결과 반환
        return StudyLogDeleteResponse.of(id);
    }
}
