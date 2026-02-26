package com.study.my_spring_study_diary.exception;

import com.study.my_spring_study_diary.global.exception.BusinessException;
import com.study.my_spring_study_diary.global.exception.ErrorCode;

public class InvalidPageRequestException extends BusinessException {

    public InvalidPageRequestException(int requestedPage, int totalPages) {
        super(ErrorCode.INVALID_PAGE,
                String.format(
                "잘못된 페이지 요청입니다. 요청 페이지: %d, 전체 페이지: %d (0~%d)",
                requestedPage, totalPages, totalPages -1
        ));
    }
}
