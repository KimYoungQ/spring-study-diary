package com.study.my_spring_study_diary.global.security.jwt;

import com.study.my_spring_study_diary.auth.exception.ExpiredTokenException;
import com.study.my_spring_study_diary.auth.exception.InvalidTokenException;
import com.study.my_spring_study_diary.global.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.error("Unauthorized access attempt: {}", authException.getMessage());

        // JwtAuthenticationFilter에서 저장한 예외를 꺼낸다
        Exception exception = (Exception) request.getAttribute("exception");

        String errorCode;
        String errorMessage;

        // 예외 타입에 따라 다른 에러 코드와 메시지를 설정
        if (exception instanceof ExpiredTokenException) {
            errorCode = "TOKEN_EXPIRED";
            errorMessage = exception.getMessage();
        } else if (exception instanceof InvalidTokenException) {
            errorCode = "INVALID_TOKEN";
            errorMessage = exception.getMessage();
        } else {
            errorCode = "UNAUTHORIZED";
            errorMessage = "Authentication is required to access this resource";
        }

        // ApiResponse 형식으로 에러 응답 생성
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode, errorMessage);

        // JSON 응답 반환
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
