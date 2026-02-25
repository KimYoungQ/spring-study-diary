package com.study.my_spring_study_diary.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for enum validation
 * Enum 타입 검증을 위한 커스텀 어노테이션
 *
 * 사용 예시:
 * @EnumValid(enumClass = Category.class, message = "유효하지 않은 카테고리입니다")
 * private String category;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented
public @interface EnumValid {

    String message() default "유효하지 않은 값입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Enum 클래스 타입
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * null을 허용할지 여부 (기본값: false)
     */
    boolean allowNull() default false;

    /**
     * 대소문자를 무시할지 여부 (기본값: false)
     */
    boolean ignoreCase() default false;
}
