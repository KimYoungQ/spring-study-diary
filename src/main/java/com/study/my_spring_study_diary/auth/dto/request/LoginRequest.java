package com.study.my_spring_study_diary.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "아이디는 필수입니다")
    private String username;  // username 또는 email

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
