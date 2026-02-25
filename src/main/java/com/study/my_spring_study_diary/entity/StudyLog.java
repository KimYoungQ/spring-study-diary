package com.study.my_spring_study_diary.entity;


import com.study.my_spring_study_diary.dto.request.StudyLogUpdateRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyLog {

    private Long id;
    private String title;
    private String content;
    private Category category;
    private Understanding understanding;
    private Integer studyTime;
    private LocalDate studyDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 전체 필드 생성자
    public StudyLog(Long id, String title, String content, Category category,
                    Understanding understanding, Integer studyTime, LocalDate studyDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.understanding = understanding;
        this.studyTime = studyTime;
        this.studyDate = studyDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 학습 일지 정보 수정
     * <p>
     * null이 아닌 값만 업데이트합니다.
     * 이 방식을 "Dirty Checking" 또는 "Partial Update"라고 합니다.
     */
    public void update(StudyLogUpdateRequest studyLogUpdateRequest) {
        Optional.ofNullable(studyLogUpdateRequest.getTitle()).ifPresent(this::setTitle);
        Optional.ofNullable(studyLogUpdateRequest.getContent()).ifPresent(this::setContent);
        Optional.ofNullable(studyLogUpdateRequest.getCategory())
                .map(String::toUpperCase)
                .map(Category::valueOf)
                .ifPresent(this::setCategory);
        Optional.ofNullable(studyLogUpdateRequest.getCategory())
                .map(String::toUpperCase)
                .map(Category::valueOf)
                .ifPresent(this::setCategory);
        Optional.ofNullable(studyLogUpdateRequest.getStudyTime()).ifPresent(this::setStudyTime);
        Optional.ofNullable(studyLogUpdateRequest.getStudyDate()).ifPresent(this::setStudyDate);

        this.updatedAt = LocalDateTime.now();
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Category getCategory() { return category; }
    public Understanding getUnderstanding() { return understanding; }
    public Integer getStudyTime() { return studyTime; }
    public LocalDate getStudyDate() { return studyDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setter 메서드들
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCategory(Category category) { this.category = category; }
    public void setUnderstanding(Understanding understanding) { this.understanding = understanding; }
    public void setStudyTime(Integer studyTime) { this.studyTime = studyTime; }
    public void setStudyDate(LocalDate studyDate) { this.studyDate = studyDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

