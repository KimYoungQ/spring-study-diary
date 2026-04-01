package com.study.my_spring_study_diary.study_log.dto.request;

import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.study_log.validation.EnumValid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 학습 일지 수정 요청 DTO
 *
 * CREATE와 달리 모든 필드가 선택적입니다.
 * null이면 기존 값을 유지합니다.
 */
@Getter
@NoArgsConstructor
public class StudyLogUpdateRequest {

    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하여야 합니다")
    private String title;          // null means keep existing value

    @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하여야 합니다")
    private String content;        // null means keep existing value

    @EnumValid(enumClass = Category.class, message = "카테고리는 JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC 중 하나여야 합니다")
    private String category;       // null means keep existing value

    @EnumValid(enumClass = Understanding.class, message = "이해도는 VERY_GOOD, GOOD, NORMAL, BAD, VERY_BAD 중 하나여야 합니다")
    private String understanding;  // null means keep existing value

    @Positive(message = "학습 시간은 양수여야 합니다")
    @Max(value = 1440, message = "학습 시간은 1440분(24시간)을 초과할 수 없습니다")
    private Integer studyTime;     // null means keep existing value

    @PastOrPresent(message = "학습 날짜는 현재 또는 과거여야 합니다")
    private LocalDate studyDate;   // null means keep existing value

    /**
     * 모든 필드가 null인지 확인
     * 아무것도 수정할 내용이 없는 경우 체크용
     */
    public boolean hasNoUpdates() {
        return title == null
                && content == null
                && category == null
                && understanding == null
                && studyTime == null
                && studyDate == null;
    }
}
