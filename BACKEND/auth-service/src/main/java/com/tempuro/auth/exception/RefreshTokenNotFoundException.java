package com.tempuro.auth.exception;

public class RefreshTokenNotFoundException extends Exception{
    
    public RefreshTokenNotFoundException(String message){
        super(message);
    }

    public RefreshTokenNotFoundException(String message, Exception e){
        super(message, e);
    }

}
