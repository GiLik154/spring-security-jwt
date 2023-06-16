package com.security.jwt.security.exception;

public class JwtTokenValidationException extends RuntimeException {
    public JwtTokenValidationException(String msg) {
        super(msg);
    }
}
