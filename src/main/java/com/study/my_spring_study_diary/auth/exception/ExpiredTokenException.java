package com.study.my_spring_study_diary.auth.exception;

/**
 * Expired Token Exception
 * Thrown when JWT token has expired
 */
public class ExpiredTokenException extends AuthException {

    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
