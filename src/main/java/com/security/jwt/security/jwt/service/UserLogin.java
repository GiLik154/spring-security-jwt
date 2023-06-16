package com.security.jwt.security.jwt.service;

import com.security.jwt.security.jwt.dto.JwtTokenDto;

public interface UserLogin {
    JwtTokenDto login(String username, String password);
}
