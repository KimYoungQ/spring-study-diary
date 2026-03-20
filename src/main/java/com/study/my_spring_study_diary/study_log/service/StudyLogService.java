package com.study.my_spring_study_diary.study_log.service;

import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.exception.ResourceNotFoundException;
import com.study.my_spring_study_diary.study_log.repository.StudyLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    private final StudyLogRepository studyLogRepository;

    @Transactional
    public StudyLog create(StudyLogCreateRequest request) {
        StudyLog studyLog = StudyLog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .studyDate(request.getStudyDate())
                .studyTime(request.getStudyTime())
                .category(Category.valueOf(request.getCategory()))
                .understanding(Understanding.valueOf(request.getUnderstanding()))
                .build();

        return studyLogRepository.save(studyLog);
    }

    public List<StudyLog> findAll() {
        return studyLogRepository.findAll();
    }

    public StudyLog findById(Long id) {
        return studyLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudyLog not found", id));
    }

    public List<StudyLog> findByCategory(Category category) {
        return studyLogRepository.findByCategory(category);
    }

    public List<StudyLog> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.findByDateRange(startDate, endDate);
    }

    public List<StudyLog> search(String title, Category category,
                                 LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.searchWithConditions(title, category, startDate, endDate);
    }


}
