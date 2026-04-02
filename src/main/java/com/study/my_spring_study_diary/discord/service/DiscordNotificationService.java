package com.study.my_spring_study_diary.discord.service;

import com.study.my_spring_study_diary.discord.dto.DiscordWebhookMessage;
import com.study.my_spring_study_diary.event.study.StudyLogCreatedEvent;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

    private final DiscordWebhookSender webhookSender;

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

    public void sendStudyLogCreatedNotification(StudyLogCreatedEvent event) {
        if (!webhookEnabled) {
            log.debug("Discord webhook is disabled");
            return;
        }

        try {
            DiscordWebhookMessage message = createStudyLogEmbed(
                    event,
                    "새로운 학습 일지가 작성되었습니다! ✨",
                    COLOR_SUCCESS
            );

            // 별도 Bean을 통해 호출 → 프록시를 거치므로 @CircuitBreaker 정상 작동
            webhookSender.send(message);
            log.info("Discord 알림이 발송 되었습니다! ID: {}", event.getStudyLogId());

        } catch (Exception e) {
            log.error("Failed to send Discord notification", e);
        }
    }

    /**
     * StudyLog를 Discord Embed로 변환
     */
    private DiscordWebhookMessage createStudyLogEmbed(StudyLogCreatedEvent event,
                                                      String title,
                                                      int color) {
        List<DiscordWebhookMessage.Field> fields = new ArrayList<>();

        // 필드 추가
        fields.add(DiscordWebhookMessage.Field.builder()
                .name("📚 제목")
                .value(event.getTitle())
                .inline(false)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("📂 카테고리")
                .value(event.getCategory().getIcon() + " " +
                        event.getCategory().name())
                .inline(true)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("💡 이해도")
                .value(event.getUnderstanding().getEmoji() + " " +
                        event.getUnderstanding().name())
                .inline(true)
                .build());

        fields.add(DiscordWebhookMessage.Field.builder()
                .name("⏱️ 학습 시간")
                .value(event.getStudyTime() + "분")
                .inline(true)
                .build());

        // 내용 (1000자 제한)
        String content = event.getContent();
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
                        .text("Study Log ID: " + event.getStudyLogId())
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

            webhookSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Test notification failed", e);
            return false;
        }
    }
}