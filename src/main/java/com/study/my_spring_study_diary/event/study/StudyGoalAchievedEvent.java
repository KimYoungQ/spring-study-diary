package com.study.my_spring_study_diary.event.study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class StudyGoalAchievedEvent {

    private final Long userId;
    private final GoalType goalType;
    private final int achievedValue;
    private final LocalDate achievedDate;

    public enum GoalType {
        DAILY_STUDY_TIME("일일 학습 시간 달성"),
        WEEKLY_STUDY_DAYS("주간 학습 일수 달성"),
        MONTHLY_LOGS("월간 학습 기록 수 달성"),
        CATEGORY_MASTER("카테고리 마스터"),
        CONSECUTIVE_DAYS("연속 학습 일수");

        private final String description;

        GoalType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
