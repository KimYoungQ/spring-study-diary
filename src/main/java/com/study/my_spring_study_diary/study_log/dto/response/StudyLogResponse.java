package com.study.my_spring_study_diary.study_log.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StudyLogResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String categoryIcon;
    private String understanding;
    private String understandingEmoji;
    private Integer studyTime;
    private LocalDate studyDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
