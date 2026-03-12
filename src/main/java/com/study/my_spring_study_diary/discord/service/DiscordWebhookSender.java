package com.study.my_spring_study_diary.discord.service;

import com.google.common.util.concurrent.RateLimiter;
import com.study.my_spring_study_diary.discord.dto.DiscordWebhookMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Discord Webhook 전송 담당 (서킷브레이커 적용)
 *
 * 별도 Bean으로 분리한 이유:
 * Spring AOP는 프록시 기반이라, 같은 클래스 내부 호출(self-invocation)에서는
 * @CircuitBreaker 같은 AOP 어노테이션이 작동하지 않음.
 * 외부 Bean에서 호출해야 프록시를 통해 서킷브레이커가 정상 작동함.
 */
@Slf4j
@Component
public class DiscordWebhookSender {

    private final RestClient discordRestClient;

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final RateLimiter rateLimiter = RateLimiter.create(0.5); // 초당 0.5개

    public DiscordWebhookSender(@Qualifier("discordRestClient") RestClient discordRestClient) {
        this.discordRestClient = discordRestClient;
    }

    /**
     * Discord Webhook으로 메시지 전송
     *
     * @CircuitBreaker 동작 흐름:
     * 1. 정상 상태(CLOSED): send 정상 실행
     * 2. 실패 누적: 설정된 실패율 초과 → 서킷 OPEN
     * 3. 서킷 OPEN: send 호출하지 않고 fallback 즉시 실행
     * 4. 대기 시간 후 HALF_OPEN: 일부만 시도 → 성공하면 CLOSED, 실패하면 다시 OPEN
     */
    @CircuitBreaker(name = "discordWebhook", fallbackMethod = "sendFallback")
    public void send(DiscordWebhookMessage message) {

        if (!rateLimiter.tryAcquire()) {
            log.warn("Discord rate limit exceeded, skipping notification");
            return;
        }

        discordRestClient.post()
                .uri(webhookUrl)
                .body(message)
                .retrieve()
                .toBodilessEntity();

        log.debug("Discord webhook message sent successfully");
    }

    /**
     * 폴백 메서드 규칙:
     * 1. 반환 타입이 원본 메서드와 동일해야 함
     * 2. 원본 메서드의 파라미터 + 마지막에 Throwable 파라미터 추가
     */
    private void sendFallback(DiscordWebhookMessage message, Throwable t) {
        log.error("디스코드 서비스 장애 - 서킷브레이커 폴백 실행: {}", t.getMessage());
    }
}