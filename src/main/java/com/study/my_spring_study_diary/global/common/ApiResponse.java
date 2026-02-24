package com.study.my_spring_study_diary.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorInfo error;

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        boolean isSuccess = true;
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .data(data)
                .build();
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> error(String code, String message) {
        boolean isSuccess = false;
        return ApiResponse.<T>builder()
                .success(isSuccess)
                .error(ErrorInfo.of(code, message))
                .build();
    }

    // 에러 정보 내부 클래스
    public static class ErrorInfo {
        private String code;
        private String message;

        private ErrorInfo(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static ErrorInfo of(String code, String message) {
            return new ErrorInfo(code, message);
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}
