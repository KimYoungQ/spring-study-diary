package com.study.my_spring_study_diary.study_log.controller;

import com.study.my_spring_study_diary.global.Security.jwt.JwtAuthenticationEntryPoint;
import com.study.my_spring_study_diary.global.Security.jwt.JwtTokenProvider;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.dto.response.StudyLogResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    }
}
