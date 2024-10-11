package com.example.springjwt.jwt.exception;

public class TokenException extends RuntimeException{
    public TokenException(String message) {
        super(message);
    }
}
