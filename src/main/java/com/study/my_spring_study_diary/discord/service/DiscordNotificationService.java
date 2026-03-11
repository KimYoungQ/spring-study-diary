package com.study.my_spring_study_diary.discord.service;

import com.study.my_spring_study_diary.discord.dto.DiscordWebhookMessage;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

    @Qualifier("discordRestClient")
    private final RestClient discordRestClient;

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    @Value("${discord.webhook.enabled}")
    private boolean webhookEnabled;

    @Value("${discord.webhook.username}")
    private String botUsername;

    @Value("${discord.webhook.avatar-url}")
    private String avatarUrl;

    // 색상 코드 상수
    private static final int COLOR_SUCCESS = 0x00FF00;  // 녹색
    private static final int COLOR_INFO = 0x3498DB;     // 파란색
    private static final int COLOR_WARNING = 0xFFD700;  // 금색
    private static final int COLOR_ERROR = 0xFF0000;    // 빨간색

    /**
     * StudyLog 생성 알림 (비동기)
     *
     * @Async를 사용하여 메인 로직을 블로킹하지 않음
     */
    @Async
    public void sendStudyLogCreatedNotification(StudyLog studyLog) {
        if (!webhookEnabled) {
            log.debug("Discord webhook is disabled");
            return;
        }

        try {
            DiscordWebhookMessage message = createStudyLogEmbed(
                    studyLog,
                    "새로운 학습 일지가 작성되었습니다! ✨",
                    COLOR_SUCCESS
            );

            sendWebhookMessage(message);
            log.info("Discord notification sent for StudyLog ID: {}", studyLog.getId());

        } catch (Exception e) {
            // Discord 실패가 메인 로직에 영향 X
            log.error("Failed to send Discord notification", e);
        }
    }

    /**
     * StudyLog를 Discord Embed로 변환
     */
    private DiscordWebhookMessage createStudyLogEmbed(StudyLog studyLog,
                                                      String title,
                                                      int color) {
        List<DiscordWebhookMessage.Field> fields = new ArrayList<>();

        // 필드 추가
        fields.add(DiscordWebhookMessage.Field.builder()
                .name("📚 제목")
                .value(studyLog.getTitle())
                .inline(false)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("📂 카테고리")
                .value(studyLog.getCategory().getIcon() + " " +
                        studyLog.getCategory().name())
                .inline(true)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("💡 이해도")
                .value(studyLog.getUnderstanding().getEmoji() + " " +
                        studyLog.getUnderstanding().name())
                .inline(true)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("⏱️ 학습 시간")
                .value(studyLog.getStudyTime() + "분")
                .inline(true)
                .build());

        // 내용 (1000자 제한)
        String content = studyLog.getContent();
        if (content != null && content.length() > 1000) {
            content = content.substring(0, 997) + "...";
        }
        fields.add(DiscordWebhookMessage.Field.builder()
                .name("📝 내용")
                .value(content != null ? content : "내용 없음")
                .inline(false)
                .build());

        // Embed 생성
        DiscordWebhookMessage.Embed embed = DiscordWebhookMessage.Embed.builder()
                .title(title)
                .color(color)
                .fields(fields)
                .footer(DiscordWebhookMessage.Footer.builder()
                        .text("Study Log ID: " + studyLog.getId())
                        .build())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        // 메시지 생성
        return DiscordWebhookMessage.builder()
                .username(botUsername)
                .avatarUrl(avatarUrl)
                .embeds(List.of(embed))
                .build();
    }

    /**
     * Discord Webhook으로 메시지 전송
     */
    private void sendWebhookMessage(DiscordWebhookMessage message) {
        discordRestClient.post()
                .uri(webhookUrl)
                .body(message)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Discord API Client Error: {}", response.getStatusCode());
                    throw new RestClientException("Discord API 요청 실패");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("Discord API Server Error: {}", response.getStatusCode());
                    throw new RestClientException("Discord 서버 오류");
                })
                .toBodilessEntity();  // Discord는 응답 본문 없음

        log.debug("Discord webhook message sent successfully");
    }

    /**
     * 테스트 알림 전송
     */
    public boolean sendTestNotification() {
        if (!webhookEnabled) {
            return false;
        }

        try {
            DiscordWebhookMessage message = DiscordWebhookMessage.builder()
                    .username(botUsername)
                    .avatarUrl(avatarUrl)
                    .content("🎉 Discord 연동 테스트 성공! Study Log Bot이 정상 작동합니다.")
                    .build();

            sendWebhookMessage(message);
            return true;
        } catch (Exception e) {
            log.error("Test notification failed", e);
            return false;
        }
    }
}
