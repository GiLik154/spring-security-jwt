package com.security.jwt.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

/**
 * 사용자 등급을 표현하기 위한 열거형 클래스입니다.
 * {@link GrantedAuthority}를 상속받아야 하며, "ROLE_ADMIN"과 같은 형식으로 권한을 반환해야 스프링 시큐리티에서 인식합니다.
 */
@Getter
public enum UserGrade implements GrantedAuthority {
    GUEST,
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}

