package com.study.my_spring_study_diary.dto.response;

import com.study.my_spring_study_diary.entity.StudyLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Setter
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

    // 기본 생성자
    public StudyLogResponse() {
    }

    // Entity → Response 변환 (정적 팩토리 메서드)
    public static StudyLogResponse from(StudyLog studyLog) {
        StudyLogResponse response = new StudyLogResponse();
        response.id = studyLog.getId();
        response.title = studyLog.getTitle();
        response.content = studyLog.getContent();
        response.category = studyLog.getCategory().name();
        response.categoryIcon = studyLog.getCategory().getIcon();
        response.understanding = studyLog.getUnderstanding().name();
        response.understandingEmoji = studyLog.getUnderstanding().getEmoji();
        response.studyTime = studyLog.getStudyTime();
        response.studyDate = studyLog.getStudyDate();
        response.createdAt = studyLog.getCreatedAt();
        response.updatedAt = studyLog.getUpdatedAt();
        return response;
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getCategoryIcon() { return categoryIcon; }
    public String getUnderstanding() { return understanding; }
    public String getUnderstandingEmoji() { return understandingEmoji; }
    public Integer getStudyTime() { return studyTime; }
    public LocalDate getStudyDate() { return studyDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
