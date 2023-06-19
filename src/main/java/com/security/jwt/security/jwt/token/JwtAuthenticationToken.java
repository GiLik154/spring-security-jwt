package com.security.jwt.security.jwt.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

/**
 * 인증을 위한 Authentication 클래스.
 */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    /** 저장되는 토큰 */
    private final String token;

    /** 판별이 가능한 정보 (여기서는 username을 사용) */
    private String principal;

    /** 다른 정보도 담을 수 있다는 것을 보여주기 위한 변수 */
    private String password;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
    }

    /** authorities는 유저의 권한을 담아서 보낼 수 있음. */
    public JwtAuthenticationToken(String token, String principal, String password,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = principal;
        this.password = password;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(token, that.token)
                && Objects.equals(principal, that.principal)
                && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, principal, password);
    }
}