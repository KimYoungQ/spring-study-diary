package com.study.my_spring_study_diary.study_log.controller;

import com.study.my_spring_study_diary.global.Security.jwt.JwtAuthenticationEntryPoint;
import com.study.my_spring_study_diary.global.Security.jwt.JwtTokenProvider;
import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogDeleteResponse;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.study_log.exception.ResourceNotFoundException;
import com.study.my_spring_study_diary.study_log.service.StudyLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudyLogController.class)
@DisplayName("StudyLogController 테스트")
class StudyLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private StudyLogService studyLogService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private StudyLogCreateRequest validRequest;
    private StudyLogResponse mockResponse;
    private StudyLogUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        validRequest = StudyLogCreateRequest.builder()
                .title("Spring MVC 학습")
                .content("Spring MVC 동작 원리를 학습하였습니다. DispatcherServlet 흐름을 이해하였습니다.")
                .category("SPRING")
                .understanding("GOOD")
                .studyTime(90)
                .studyDate(LocalDate.of(2026, 4, 3))
                .build();

        mockResponse = StudyLogResponse.builder()
                .id(1L)
                .title("Spring MVC 학습")
                .content("Spring MVC 동작 원리를 학습하였습니다. DispatcherServlet 흐름을 이해하였습니다.")
                .category("SPRING")
                .categoryIcon("🌱")
                .understanding("GOOD")
                .understandingEmoji("😊")
                .studyTime(90)
                .studyDate(LocalDate.of(2026, 4, 3))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updateRequest = StudyLogUpdateRequest.builder()
                .title("Spring Boot 테스트 학습 (수정)")
                .content("MockMvc를 사용한 컨트롤러 테스트 작성 방법을 심화 학습했습니다.")
                .studyDate(LocalDate.now())
                .category("SPRING")
                .understanding("VERY_GOOD")
                .studyTime(180)
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/logs - 학습 일지 생성")
    class CreateStudyLog {

        @Test
        @WithMockUser
        @DisplayName("생성 성공 - 201 반환")
        void createStudyLog_Success_Returns201() throws Exception {

            // Given
            when(studyLogService.createStudyLog(any(StudyLogCreateRequest.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/logs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.title").value("Spring MVC 학습"))
                    .andExpect(jsonPath("$.data.category").value("SPRING"))
                    .andExpect(jsonPath("$.data.understanding").value("GOOD"))
                    .andExpect(jsonPath("$.data.studyTime").value(90));
        }

        @Test
        @DisplayName("학습 일지 생성 - 유효성 검증 실패 (짧은 내용)")
        @WithMockUser
        void createStudyLog_ValidationFailed_ShortContent() throws Exception {
            // Given
            StudyLogCreateRequest invalidRequest = StudyLogCreateRequest.builder()
                    .title("테스트 제목")
                    .content("짧은 내용")  // 10자 미만
                    .studyDate(LocalDate.now())
                    .category("SPRING")
                    .understanding("GOOD")
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/logs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("READ 작업")
    class ReadOperations {

        @Test
        @DisplayName("모든 학습 일지 조회 - 성공")
        @WithMockUser
        void getAllStudyLogs_Success() throws Exception {
            // Given
            List<StudyLogResponse> responses = Arrays.asList(mockResponse);
            when(studyLogService.getAllStudyLogs()).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/logs"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].title").value("Spring MVC 학습"));
        }

        @Test
        @DisplayName("ID로 학습 일지 조회 - 성공")
        @WithMockUser
        void getStudyLogById_Success() throws Exception {
            // Given
            when(studyLogService.getStudyLogById(1L)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/logs/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Spring MVC 학습"));
        }

        @Test
        @DisplayName("ID로 학습 일지 조회 - 존재하지 않음")
        @WithMockUser
        void getStudyLogById_NotFound() throws Exception {
            // Given
            when(studyLogService.getStudyLogById(999L))
                    .thenThrow(new ResourceNotFoundException("StudyLog", 999L));

            // When & Then
            mockMvc.perform(get("/api/v1/logs/999"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("카테고리로 학습 일지 조회 - 성공")
        @WithMockUser
        void getStudyLogsByCategory_Success() throws Exception {
            // Given
            List<StudyLogResponse> responses = Arrays.asList(mockResponse);
            when(studyLogService.getStudyLogsByCategory("SPRING"))
                    .thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/logs/category/SPRING"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("페이징 작업")
    class PagingOperations {

        @Test
        @DisplayName("페이징 조회 - 성공")
        @WithMockUser
        void getStudyLogsPage_Success() throws Exception {
            // Given
            Page<StudyLogResponse> page = new Page<>(
                    Arrays.asList(mockResponse),
                    0,  // page
                    10, // size
                    1L  // totalElements
            );
            when(studyLogService.getStudyLogsWithPaging(0, 10)).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/logs/page")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1));
        }

        @Test
        @DisplayName("검색 및 페이징 - 성공")
        @WithMockUser
        void searchStudyLogs_Success() throws Exception {
            // Given
            Page<StudyLogResponse> page = new Page<>(
                    Arrays.asList(mockResponse),
                    0,  // page
                    10, // size
                    1L  // totalElements
            );
            when(studyLogService.searchStudyLogsWithPaging(
                    anyString(), anyString(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/logs/search")
                            .param("title", "Spring")
                            .param("category", "SPRING")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("UPDATE 작업")
    class UpdateOperations {

        @Test
        @DisplayName("학습 일지 수정 - 성공")
        @WithMockUser
        void updateStudyLog_Success() throws Exception {
            // Given
            StudyLogResponse updatedResponse = StudyLogResponse.builder()
                    .id(1L)
                    .title("Spring Boot 테스트 학습 (수정)")
                    .content("MockMvc를 사용한 컨트롤러 테스트 작성 방법을 심화 학습했습니다.")
                    .studyDate(LocalDate.now())
                    .category("SPRING")
                    .categoryIcon("🍃")
                    .understanding("VERY_GOOD")
                    .understandingEmoji("😊")
                    .studyTime(180)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(studyLogService.updateStudyLog(eq(1L), any(StudyLogUpdateRequest.class)))
                    .thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/logs/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Spring Boot 테스트 학습 (수정)"))
                    .andExpect(jsonPath("$.data.understanding").value("VERY_GOOD"));
        }

        @Test
        @DisplayName("학습 일지 수정 - ID 유효성 검증 실패")
        @WithMockUser
        void updateStudyLog_InvalidId() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/logs/0")  // 0 또는 음수 ID
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE 작업")
    class DeleteOperations {

        @Test
        @DisplayName("학습 일지 삭제 - 성공")
        @WithMockUser
        void deleteStudyLog_Success() throws Exception {
            // Given
            StudyLogDeleteResponse deleteResponse = StudyLogDeleteResponse.of(1L);
            when(studyLogService.deleteStudyLog(1L)).thenReturn(deleteResponse);

            // When & Then
            mockMvc.perform(delete("/api/v1/logs/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.deletedId").value(1));
        }

        @Test
        @DisplayName("학습 일지 삭제 - 존재하지 않음")
        @WithMockUser
        void deleteStudyLog_NotFound() throws Exception {
            // Given
            when(studyLogService.deleteStudyLog(999L))
                    .thenThrow(new ResourceNotFoundException("StudyLog",999L));

            // When & Then
            mockMvc.perform(delete("/api/v1/logs/999"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
