package com.study.my_spring_study_diary.discord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscordWebhookMessage {

    /**
     * 봇의 사용자명 (옵션)
     */
    private String username;

    /**
     * 봇의 아바타 URL (옵션)
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * 메시지 본문 (최대 2000자)
     */
    private String content;

    /**
     * Embed 메시지 리스트 (리치 콘텐츠)
     */
    private List<Embed> embeds;

    /**
     * Discord Embed - 리치 포맷 메시지
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Embed {
        private String title;
        private String description;
        private Integer color;  // 16진수 색상 (0x00FF00 = 녹색)
        private String timestamp;
        private DiscordWebhookMessage.Footer footer;
        private List<DiscordWebhookMessage.Field> fields;
    }

    /**
     * Embed 필드 - 구조화된 정보 표시
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Field {
        private String name;
        private String value;
        private boolean inline;  // true: 같은 줄, false: 새 줄
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Footer {
        private String text;
        @JsonProperty("icon_url")
        private String iconUrl;
    }
}
