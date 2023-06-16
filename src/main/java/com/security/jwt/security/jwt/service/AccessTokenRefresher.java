package com.security.jwt.security.jwt.service;

public interface AccessTokenRefresher {
    String refresh(String refreshToken);
}
