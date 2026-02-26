package com.study.my_spring_study_diary.study_log.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum 검증을 위한 Validator 구현체
 *
 * Bean Validation 관례에 따라 null은 검증을 통과시킴
 * null 체크가 필요한 경우 @NotNull을 함께 사용
 */
public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    private Set<String> validValues;
    private boolean allowNull;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        this.allowNull = constraintAnnotation.allowNull();
        this.ignoreCase = constraintAnnotation.ignoreCase();

        // Enum의 모든 값을 Set으로 변환
        this.validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(enumConstant -> {
                    if (ignoreCase) {
                        return enumConstant.name().toUpperCase();
                    }
                    return enumConstant.name();
                })
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Bean Validation 관례: null은 검증 통과
        // null 체크가 필요하면 @NotNull을 별도로 사용
        if (value == null) {
            return allowNull;
        }

        // ignoreCase가 true면 대소문자 무시하고 비교
        if (ignoreCase) {
            return validValues.contains(value.toUpperCase());
        }

        return validValues.contains(value);
    }
}

