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

    // Individual update methods for MapStruct
    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCategory(Category category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUnderstanding(Understanding understanding) {
        this.understanding = understanding;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStudyTime(Integer studyTime) {
        this.studyTime = studyTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStudyDate(LocalDate studyDate) {
        this.studyDate = studyDate;
        this.updatedAt = LocalDateTime.now();
    }
}

