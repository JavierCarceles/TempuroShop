package com.tempuro.auth.exception;

public class UserPersistenceException extends RuntimeException {

    public UserPersistenceException(String message, Exception e) {
        super(message, e);
    }
}
