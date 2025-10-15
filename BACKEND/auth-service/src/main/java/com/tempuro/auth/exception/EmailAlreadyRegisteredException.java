package com.tempuro.auth.exception;

public class EmailAlreadyRegisteredException extends Exception {
    
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }

    public EmailAlreadyRegisteredException(String message, Exception e) {
        super(message, e);
    }

}
