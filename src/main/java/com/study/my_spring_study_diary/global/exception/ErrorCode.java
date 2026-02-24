package com.study.my_spring_study_diary.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("INVALID_INPUT", "입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),

    // 커스텀 에러
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "resouce not found", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "duplicate resource", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
