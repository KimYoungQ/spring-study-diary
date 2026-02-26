package com.study.my_spring_study_diary.study_log.dto.response;

import lombok.Builder;

@Builder
public class StudyLogDeleteResponse {

    private String message;
    private Long deletedId;

    public static StudyLogDeleteResponse of(Long id) {
        return StudyLogDeleteResponse.builder()
                .deletedId(id)
                .build();
    }
}
