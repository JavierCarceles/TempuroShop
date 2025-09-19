package com.tempuro.auth.exception;

public class JwtGenerationException extends RuntimeException{
    
    public JwtGenerationException(String meessage){
        super(meessage);
    }

    public JwtGenerationException(String message, Exception e){
        super(message, e);
    }

}
