package com.study.my_spring_study_diary.study_log.service;

import com.study.my_spring_study_diary.event.study.StudyLogCreatedEvent;
import com.study.my_spring_study_diary.global.mapper.StudyLogMapper;
import com.study.my_spring_study_diary.study_log.dao.StudyLogDao;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.discord.service.DiscordNotificationService;
import com.study.my_spring_study_diary.study_log.exception.InvalidPageRequestException;
import com.study.my_spring_study_diary.study_log.exception.ResourceNotFoundException;
import com.study.my_spring_study_diary.global.common.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    private StudyLogCreateRequest testRequest;

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

        // 테스트용 request 생성
        testRequest = StudyLogCreateRequest.builder()
                .title("New Study")
                .content("New Content for testing study log creation")
                .category("SPRING")
                .understanding("VERY_GOOD")
                .studyTime(90)
                .studyDate(LocalDate.now())
                .build();
    }

    @Nested
    @DisplayName("학습 기록 생성")
    class createStudyLog {
        @Test
        @DisplayName("생성 성공")
        void createStudyLog_Success() {
            // Given
            when(studyLogMapper.toEntity(any(StudyLogCreateRequest.class))).thenReturn(testStudyLog);
            when(studyLogDao.save(any(StudyLog.class))).thenReturn(testStudyLog);
            when(studyLogMapper.toResponse(any(StudyLog.class))).thenReturn(testResponse);

            // When
            StudyLogResponse response = studyLogService.createStudyLog(testRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Test Title");

            verify(studyLogMapper).toEntity(any(StudyLogCreateRequest.class));
            verify(studyLogDao).save(any(StudyLog.class));
            verify(studyLogMapper).toResponse(any(StudyLog.class));
        }

        @Test
        @DisplayName("이벤트 발행 확인")
        void createStudyLog_publishEvent() {

            when(studyLogMapper.toEntity(any(StudyLogCreateRequest.class))).thenReturn(testStudyLog);
            when(studyLogDao.save(any(StudyLog.class))).thenReturn(testStudyLog);
            when(studyLogMapper.toResponse(any(StudyLog.class))).thenReturn(testResponse);

            studyLogService.createStudyLog(testRequest);

            verify(eventPublisher).publishEvent(any(StudyLogCreatedEvent.class));
        }
    }

    @Nested
    @DisplayName("ID로 학습 기록 조회")
    class findStudyLogByID {
        @Test
        @DisplayName("ID로 조회 - 성공")
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

            verify(studyLogDao).findById(1L);
            verify(studyLogMapper).toResponse(testStudyLog);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 - 실패")
        void getStudyLogById_throwsException() {

            // Given
            when(studyLogDao.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> studyLogService.getStudyLogById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found with ID:");
        }
    }

    @Nested
    @DisplayName("전체 학습 기록 조회")
    class findStudyLogByAll {

        @Test
        @DisplayName("전체 조회 성공")
        void getAllStudyLogs_Success() {

            // Given
            List<StudyLog> studyLogAll = new ArrayList<>();
            studyLogAll.add(testStudyLog);
            studyLogAll.add(testStudyLog);

            List<StudyLogResponse> studyLogAllResponse = new ArrayList<>();
            studyLogAllResponse.add(testResponse);
            studyLogAllResponse.add(testResponse);

            when(studyLogDao.findAll()).thenReturn(studyLogAll);
            when(studyLogMapper.toResponseList(studyLogAll)).thenReturn(studyLogAllResponse);

            // When
            List<StudyLogResponse> response = studyLogService.getAllStudyLogs();

            // Then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getTitle()).isEqualTo("Test Title");
            assertThat(response.get(0).getCategory()).isEqualTo("SPRING");

            verify(studyLogDao).findAll();
            verify(studyLogMapper).toResponseList(studyLogAll);
        }

        @Test
        @DisplayName("학습 기록이 없을 때 빈 리스트 반환")
        void getAllStudyLogs_ReturnsEmptyList() {

            // Given
            when(studyLogDao.findAll()).thenReturn(new ArrayList<>());
            when(studyLogMapper.toResponseList(new ArrayList<>())).thenReturn(new ArrayList<>());

            // When
            List<StudyLogResponse> response = studyLogService.getAllStudyLogs();

            // Then
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("페이징 조회")
    class getStudyLogsWithPaging {

        @Test
        @DisplayName("정상 페이징 조회")
        void getStudyLogsWithPaging_Success() {
            // Given
            int page = 0, size = 10;
            List<StudyLog> studyLogs = new ArrayList<>();
            studyLogs.add(testStudyLog);
            studyLogs.add(testStudyLog);
            Page<StudyLog> studyLogPage = new Page<>(studyLogs, page, size, 2L);

            when(studyLogDao.findAllWithPaging(page, size)).thenReturn(studyLogPage);
            when(studyLogMapper.toResponse(testStudyLog)).thenReturn(testResponse);

            // When
            Page<StudyLogResponse> result = studyLogService.getStudyLogsWithPaging(page, size);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getPage()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(2L);

            verify(studyLogDao).findAllWithPaging(page, size);
        }

        @Test
        @DisplayName("페이지 범위 초과 시 예외처리")
        void getStudyLogsWithPaging_throwsExceptionWhenPageExceedsTotalPages() {
            // Given
            int page = 5, size = 10;
            // totalElements=10, size=10 → totalPages=1 이므로 page=5는 범위 초과
            Page<StudyLog> studyLogPage = new Page<>(new ArrayList<>(), page, size, 10L);

            when(studyLogDao.findAllWithPaging(page, size)).thenReturn(studyLogPage);

            // When & Then
            assertThatThrownBy(() -> studyLogService.getStudyLogsWithPaging(page, size))
                    .isInstanceOf(InvalidPageRequestException.class);
        }

        @Test
        @DisplayName("size가 MAX_PAGE_SIZE(100) 초과 시 100으로 클램핑")
        void getStudyLogsWithPaging_clampsSizeToMax() {
            // Given
            int page = 0, oversizedSize = 200, clampedSize = 100;
            Page<StudyLog> studyLogPage = new Page<>(List.of(testStudyLog), page, clampedSize, 1L);

            when(studyLogDao.findAllWithPaging(page, clampedSize)).thenReturn(studyLogPage);
            when(studyLogMapper.toResponse(testStudyLog)).thenReturn(testResponse);

            // When
            Page<StudyLogResponse> result = studyLogService.getStudyLogsWithPaging(page, oversizedSize);

            // Then
            assertThat(result.getSize()).isEqualTo(clampedSize);
            verify(studyLogDao).findAllWithPaging(page, clampedSize);
        }

        @Test
        @DisplayName("size가 0 이하일 때 1로 클램핑")
        void getStudyLogsWithPaging_clampsSizeToMin() {
            // Given
            int page = 0, invalidSize = 0, clampedSize = 1;
            Page<StudyLog> studyLogPage = new Page<>(List.of(testStudyLog), page, clampedSize, 1L);

            when(studyLogDao.findAllWithPaging(page, clampedSize)).thenReturn(studyLogPage);
            when(studyLogMapper.toResponse(testStudyLog)).thenReturn(testResponse);

            // When
            Page<StudyLogResponse> result = studyLogService.getStudyLogsWithPaging(page, invalidSize);

            // Then
            assertThat(result.getSize()).isEqualTo(clampedSize);
            verify(studyLogDao).findAllWithPaging(page, clampedSize);
        }
    }

    @Nested
    @DisplayName("학습 기록 수정")
    class updateStudyLog {

        private StudyLogUpdateRequest updateRequest;

        @BeforeEach
        void setUpUpdateRequest() {
            updateRequest = StudyLogUpdateRequest.builder()
                    .title("Updated Title")
                    .content("Updated content for testing partial update")
                    .build();
        }

        @Test
        @DisplayName("일부 필드만 수정 성공")
        void updateStudyLog_Success() {
            // Given
            when(studyLogDao.findById(1L)).thenReturn(Optional.of(testStudyLog));
            when(studyLogDao.update(testStudyLog)).thenReturn(testStudyLog);
            when(studyLogMapper.toResponse(testStudyLog)).thenReturn(testResponse);

            // When
            StudyLogResponse response = studyLogService.updateStudyLog(1L, updateRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            verify(studyLogDao).findById(1L);
            verify(studyLogMapper).partialUpdate(updateRequest, testStudyLog);
            verify(studyLogDao).update(testStudyLog);
            verify(studyLogMapper).toResponse(testStudyLog);
        }

        @Test
        @DisplayName("존재하지 않는 ID 수정 시 ResourceNotFoundException")
        void updateStudyLog_throwsExceptionWhenNotFound() {
            // Given
            when(studyLogDao.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> studyLogService.updateStudyLog(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(studyLogDao).findById(999L);
        }

        @Test
        @DisplayName("모든 필드가 null이면 IllegalArgumentException")
        void updateStudyLog_throwsExceptionWhenAllFieldsAreNull() {
            // Given
            StudyLogUpdateRequest emptyRequest = StudyLogUpdateRequest.builder().build();

            when(studyLogDao.findById(1L)).thenReturn(Optional.of(testStudyLog));

            // When & Then
            assertThatThrownBy(() -> studyLogService.updateStudyLog(1L, emptyRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수정할 내용이 없습니다.");
        }
    }
}