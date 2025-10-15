package com.tempuro.auth.exception;

public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(String message, Exception e){
        super(message, e);
    }
}
