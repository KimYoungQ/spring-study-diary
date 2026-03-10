package com.study.my_spring_study_diary.event.handler;

import com.study.my_spring_study_diary.event.study.StudyGoalAchievedEvent;
import com.study.my_spring_study_diary.event.study.StudyLogCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyAnalyticsHandler {

    // 실제로는 StatisticsService를 주입받아 사용
    // private final StatisticsService statisticsService;

    /**
     * 학습 통계 업데이트 (비동기)
     */
    @Async("analyticsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Order(2)  // 알림보다 나중에 실행
    public CompletableFuture<String> updateStatistics(StudyLogCreatedEvent event) {
        log.info("📊 [통계] 학습 통계 업데이트 시작 - Category: {}, Thread: {}",
                event.getCategory(),
                Thread.currentThread().getName());

        try {
            // 통계 계산 시뮬레이션
            Thread.sleep(3000);

            // 일일 학습 시간 누적
            int dailyTotal = calculateDailyTotal(event.getStudyDate(), event.getStudyTime());
            log.info("오늘의 총 학습 시간: {}분", dailyTotal);

            // 카테고리별 통계
            updateCategoryStats(event.getCategory(), event.getStudyTime());

            // 학습 목표 달성 확인
            if (dailyTotal >= 180) {  // 3시간 이상 학습
                log.info("🎯 일일 학습 목표 달성!");
                // 목표 달성 이벤트 발행 (이벤트 체이닝)
                publishGoalAchievedEvent(event, dailyTotal);
            }

            return CompletableFuture.completedFuture("통계 업데이트 완료");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 학습 목표 달성 처리
     */
    @EventListener
    public void handleGoalAchieved(StudyGoalAchievedEvent event) {
        log.info("🏆 [목표달성] {} - {}분 달성!",
                event.getGoalType().getDescription(),
                event.getAchievedValue());

        // 뱃지 부여 로직
        grantBadge(event.getUserId(), event.getGoalType());

        // 포인트 적립 로직
        int points = calculatePoints(event.getGoalType(), event.getAchievedValue());
        log.info("💰 포인트 {}P 적립!", points);
    }

    // Helper methods (실제로는 Service로 분리)
    private int calculateDailyTotal(java.time.LocalDate date, int additionalTime) {
        // 실제로는 DB에서 해당 날짜의 총 학습 시간을 조회
        return additionalTime + 120;  // 시뮬레이션
    }

    private void updateCategoryStats(com.study.my_spring_study_diary.study_log.entity.Category category, int time) {
        log.info("카테고리 {} 통계 업데이트: +{}분", category.name(), time);
    }

    private void publishGoalAchievedEvent(StudyLogCreatedEvent event, int total) {
        // ApplicationEventPublisher를 통해 새 이벤트 발행
        log.info("목표 달성 이벤트 발행");
    }

    private void grantBadge(Long userId, StudyGoalAchievedEvent.GoalType type) {
        log.info("사용자 {}에게 {} 뱃지 부여", userId, type.name());
    }

    private int calculatePoints(StudyGoalAchievedEvent.GoalType type, int value) {
        return switch (type) {
            case DAILY_STUDY_TIME -> 100;
            case WEEKLY_STUDY_DAYS -> 500;
            case MONTHLY_LOGS -> 1000;
            case CATEGORY_MASTER -> 2000;
            case CONSECUTIVE_DAYS -> value * 50;
        };
    }
}
