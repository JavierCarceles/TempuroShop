package com.tempuro.auth.exception;

public class TokenPersistenceException extends RuntimeException{
    
    public TokenPersistenceException(String message, Exception e){
        super(message, e);
    }

}
