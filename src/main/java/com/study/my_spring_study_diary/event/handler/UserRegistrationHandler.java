package com.study.my_spring_study_diary.event.handler;

import com.study.my_spring_study_diary.event.auth.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationHandler {

    /**
     * 환영 이메일 발송 (비동기)
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<Void> sendWelcomeEmail(UserRegisteredEvent event) {
        log.info("📮 [환영] 환영 이메일 발송 시작 - User: {}", event.getUsername());

        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);  // 이메일 발송 시뮬레이션

                log.info("환영 이메일 발송 완료!");
                log.info("To: {}", event.getEmail());
                log.info("Subject: Study Diary에 오신 것을 환영합니다!");
                log.info("Content: 안녕하세요 {}님, 함께 성장하는 학습 여정을 시작해보세요!",
                        event.getUsername());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("환영 이메일 발송 실패", e);
            }
        });
    }

    /**
     * 신규 사용자 온보딩 프로세스 (비동기)
     */
    @Async("analyticsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void initializeUserData(UserRegisteredEvent event) {
        log.info("🚀 [온보딩] 사용자 초기 데이터 설정 - UserId: {}", event.getUserId());

        try {
            // 1. 기본 학습 목표 설정
            Thread.sleep(500);
            log.info("기본 학습 목표 설정 완료");

            // 2. 추천 학습 카테고리 생성
            Thread.sleep(500);
            log.info("추천 카테고리 생성 완료");

            // 3. 튜토리얼 학습 로그 생성
            Thread.sleep(500);
            log.info("튜토리얼 학습 로그 생성 완료");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("온보딩 프로세스 실패", e);
        }
    }
}