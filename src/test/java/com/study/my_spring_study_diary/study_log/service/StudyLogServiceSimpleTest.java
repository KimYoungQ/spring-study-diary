package com.study.my_spring_study_diary.study_log.service;

import com.study.my_spring_study_diary.global.mapper.StudyLogMapper;
import com.study.my_spring_study_diary.study_log.dao.StudyLogDao;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.discord.service.DiscordNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyLog Service 기본 테스트")
class StudyLogServiceSimpleTest {

    @Mock
    private StudyLogDao studyLogDao;

    @Mock
    private StudyLogMapper studyLogMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DiscordNotificationService discordNotificationService;

    @InjectMocks
    private StudyLogService studyLogService;

    private StudyLog testStudyLog;
    private StudyLogResponse testResponse;

    @BeforeEach
    void setUp() {
        // 테스트용 StudyLog 엔티티 생성
        testStudyLog = StudyLog.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .category(Category.SPRING)
            .understanding(Understanding.GOOD)
            .studyTime(60)
            .studyDate(LocalDate.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        // 테스트용 Response 생성
        testResponse = StudyLogResponse.builder()
            .id(1L)
            .title("Test Title")
            .content("Test Content")
            .category("SPRING")
            .categoryIcon("🌱")
            .understanding("GOOD")
            .understandingEmoji("😊")
            .studyTime(60)
            .studyDate(LocalDate.now())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("ID로 학습 기록 조회 - 성공")
    void getStudyLogById_Success() {
        // Given
        when(studyLogDao.findById(1L)).thenReturn(Optional.of(testStudyLog));
        when(studyLogMapper.toResponse(testStudyLog)).thenReturn(testResponse);

        // When
        StudyLogResponse response = studyLogService.getStudyLogById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Title");
        assertThat(response.getCategory()).isEqualTo("SPRING");
    }

    @Test
    @DisplayName("학습 기록 생성 - 성공")
    void createStudyLog_Success() {
        // Given
        StudyLogCreateRequest request = new StudyLogCreateRequest();
        request.setTitle("New Study");
        request.setContent("New Content for testing study log creation");
        request.setCategory("SPRING");
        request.setUnderstanding("VERY_GOOD");
        request.setStudyTime(90);
        request.setStudyDate(LocalDate.now());

        when(studyLogMapper.toEntity(any(StudyLogCreateRequest.class))).thenReturn(testStudyLog);
        when(studyLogDao.save(any(StudyLog.class))).thenReturn(testStudyLog);
        when(studyLogMapper.toResponse(any(StudyLog.class))).thenReturn(testResponse);

        // When
        StudyLogResponse response = studyLogService.createStudyLog(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Title");
    }
}