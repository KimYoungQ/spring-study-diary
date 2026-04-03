package com.study.my_spring_study_diary.auth.dto.response;

import lombok.*;

@Value
@Builder
public class SignupResponse {

    private Long id;
    private String username;
    private String email;
}

