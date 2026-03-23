package com.study.my_spring_study_diary.study_log.service;

import com.study.my_spring_study_diary.global.mapper.StudyLogMapper;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.exception.ResourceNotFoundException;
import com.study.my_spring_study_diary.study_log.repository.StudyLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
public class StudyLogJpaService {

    // 의존성 주입: Repository를 주입받음
    private final StudyLogRepository studyLogRepository;
    private final StudyLogMapper studyLogMapper;

    // // ========== CREATE ==========
    @Transactional
    public StudyLogResponse create(StudyLogCreateRequest request) {
        StudyLog studyLog = StudyLog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .studyDate(request.getStudyDate())
                .studyTime(request.getStudyTime())
                .category(Category.valueOf(request.getCategory()))
                .understanding(Understanding.valueOf(request.getUnderstanding()))
                .build();

        // 3. 저장
        StudyLog saved = studyLogRepository.save(studyLog);

        return studyLogMapper.toResponse(saved);
    }

    // ========== READ ==========

    public List<StudyLogResponse> findAll() {
        return studyLogRepository.findAll().stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudyLogResponse> findByCategory(Category category) {
        return studyLogRepository.findByCategory(category).stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudyLogResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.findByDateRange(startDate, endDate).stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudyLogResponse> search(String title, Category category,
                                 LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.searchWithConditions(title, category, startDate, endDate).stream()
                .map(studyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StudyLogResponse findById(Long id) {
        StudyLog studyLog = studyLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudyLog", id));

        return studyLogMapper.toResponse(studyLog);
    }

    @Transactional
    public StudyLogResponse update(Long id, StudyLogUpdateRequest request) {

        StudyLog studyLog = studyLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudyLog", id));

        // Dirty Checking을 통한 업데이트
        if (request.getTitle() != null) {
            studyLog.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            studyLog.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            studyLog.setCategory(Category.valueOf(request.getCategory()));
        }
        if (request.getUnderstanding() != null) {
            studyLog.setUnderstanding(Understanding.valueOf(request.getUnderstanding()));
        }
        if (request.getStudyTime() != null) {
            studyLog.setStudyTime(request.getStudyTime());
        }
        if (request.getStudyDate() != null) {
            studyLog.setStudyDate(request.getStudyDate());
        }

        // 트랜잭션 커밋 시점에 자동으로 UPDATE 쿼리 실행
        return studyLogMapper.toResponse(studyLog);
    }

    @Transactional
    public void delete(Long id) {
        if (!studyLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("StudyLog", id);
        }
        studyLogRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void bulkUpdateHours(List<Long> ids, Integer hours) {
        for (Long id : ids) {
            studyLogRepository.updateStudyHours(id, hours);
        }
    }

    @Cacheable(value = "categoryCount", key = "#category")
    public long countByCategory(Category category) {
        return studyLogRepository.countByCategory(category);
    }
}
