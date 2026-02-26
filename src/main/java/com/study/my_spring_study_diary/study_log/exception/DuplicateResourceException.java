package com.study.my_spring_study_diary.study_log.exception;

import com.study.my_spring_study_diary.global.exception.BusinessException;
import com.study.my_spring_study_diary.global.exception.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resourceName, String field) {
        super(ErrorCode.DUPLICATE_RESOURCE, String.format("%s already exists with %s", resourceName, field));
    }
}
