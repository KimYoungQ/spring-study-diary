package com.study.my_spring_study_diary.exception;

import org.springframework.http.ResponseEntity;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with ID: %d", resourceName, id));
    }
}
