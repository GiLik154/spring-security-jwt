package com.security.jwt.controller;

import com.security.jwt.security.jwt.service.AccessTokenRefresher;
import com.security.jwt.security.jwt.service.UserLogin;
import com.security.jwt.security.jwt.token.JwtAuthenticationToken;
import com.security.jwt.security.jwt.dto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {
    private static final String AUTHORIZATION = "Authorization";

    private final UserLogin userLogin;
    private final AccessTokenRefresher accessTokenRefresher;

    @GetMapping("/login")
    public String login() {
        return "thymeleaf/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> login(String username, String password) {
        JwtTokenDto dto = userLogin.login(username, password);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @ResponseBody
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserFromToken() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", jwtAuthenticationToken.getPrincipal());
        jsonMap.put("password", jwtAuthenticationToken.getPassword());

        return ResponseEntity.ok().body(jsonMap);
    }

    @ResponseBody
    @GetMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION);

        String newAccessToken = accessTokenRefresher.refresh(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }


    @GetMapping("/user")
    public String session() {

        return "thymeleaf/index";
    }

    @GetMapping("/admin")
    public String authentication() {

        return "thymeleaf/admin";
    }
}