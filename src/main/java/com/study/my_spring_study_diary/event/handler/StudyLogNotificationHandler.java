package com.study.my_spring_study_diary.event.handler;


import com.study.my_spring_study_diary.event.study.StudyLogCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyLogNotificationHandler {

    // 실제로는 EmailService, SmsService 등을 주입받아 사용
    // private final EmailService emailService;
    // private final PushNotificationService pushService;

    /**
     * 학습 기록 생성 시 알림 발송 (비동기 + 트랜잭션 커밋 후)
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStudyLogCreated(StudyLogCreatedEvent event) {
        log.info("📧 [알림] 학습 기록 알림 시작 - StudyLogId: {}, Thread: {}",
                event.getStudyLogId(),
                Thread.currentThread().getName());

        try {
            // 1. 이메일 알림 발송 시뮬레이션
            Thread.sleep(2000);  // 이메일 발송 시간 시뮬레이션
            log.info("✅ 이메일 발송 완료: '{}' 학습을 기록했습니다!", event.getTitle());

            // 2. 푸시 알림 발송 시뮬레이션
            Thread.sleep(1000);  // 푸시 알림 시간 시뮬레이션
            log.info("✅ 푸시 알림 발송 완료");

            // 실제 구현 예시:
            // emailService.send(
            //     event.getUserEmail(),
            //     "학습 기록이 저장되었습니다",
            //     String.format("오늘도 열심히 공부하셨네요! '%s' 학습을 %d분간 진행하셨습니다.",
            //         event.getTitle(), event.getStudyTime())
            // );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("알림 발송 중 인터럽트 발생", e);
        } catch (Exception e) {
            log.error("알림 발송 실패 - StudyLogId: {}", event.getStudyLogId(), e);
            // TODO: 재시도 로직 또는 실패 알림 큐에 저장
        }
    }

    /**
     * 카테고리별 학습 알림 (조건부 처리)
     * Spring Boot 관련 학습일 때만 특별 알림
     */
    @EventListener(condition = "#event.category.name() == 'SPRING'")
    public void handleSpringStudy(StudyLogCreatedEvent event) {
        log.info("🌱 Spring 학습 감지! 추가 학습 자료를 준비합니다.");
        // Spring 관련 추가 학습 자료 추천 로직
    }
}
