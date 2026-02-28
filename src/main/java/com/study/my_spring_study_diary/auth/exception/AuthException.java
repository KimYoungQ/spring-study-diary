package com.study.my_spring_study_diary.auth.exception;

/**
 * Base Authentication Exception
 * Parent class for all authentication-related exceptions
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

