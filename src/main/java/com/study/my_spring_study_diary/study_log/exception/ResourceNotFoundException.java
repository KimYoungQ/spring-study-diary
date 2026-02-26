package com.study.my_spring_study_diary.study_log.exception;

import com.study.my_spring_study_diary.global.exception.BusinessException;
import com.study.my_spring_study_diary.global.exception.ErrorCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("%s not found with ID: %d", resourceName, id));
    }
}
