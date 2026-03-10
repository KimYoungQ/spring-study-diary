package com.study.my_spring_study_diary.event.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UserRegisteredEvent {

    private final Long userId;
    private final String username;
    private final String email;
    private final LocalDateTime registeredAt;
}