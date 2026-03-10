package com.study.my_spring_study_diary.event.study;

import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class StudyLogCreatedEvent {

    private final Long studyLogId;
    private final String title;
    private final Category category;
    private final Understanding understanding;
    private final Integer studyTime;  // 분 단위
    private final LocalDate studyDate;
    private final LocalDateTime createdAt;

    // TODO: 실제로는 userId도 포함해야 함
    // private final Long userId;
    // private final String userEmail;

    /**
     * StudyLog 엔티티로부터 이벤트 생성
     */
    public static StudyLogCreatedEvent from(StudyLog studyLog) {
        return new StudyLogCreatedEvent(
                studyLog.getId(),
                studyLog.getTitle(),
                studyLog.getCategory(),
                studyLog.getUnderstanding(),
                studyLog.getStudyTime(),
                studyLog.getStudyDate(),
                studyLog.getCreatedAt()
        );
    }
}
