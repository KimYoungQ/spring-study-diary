package com.study.my_spring_study_diary.auth.exception;

/**
 * Invalid Token Exception
 * Thrown when JWT token is invalid, malformed, or has an invalid signature
 */
public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}