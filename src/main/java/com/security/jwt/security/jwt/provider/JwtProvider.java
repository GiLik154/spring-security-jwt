package com.security.jwt.security.jwt.provider;

import com.security.jwt.security.jwt.util.JwtUtil;
import com.security.jwt.security.jwt.token.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * AuthenticationProvider는 인증과 관련된 클래스이다.
 * AuthenticationProvider를 구현해서 사용하여야 스프링 시큐리티의 체인을 이용할 수 있다.
 */
@Component
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;

    /**
     * JwtFilter에서 authentication를 받아오는데, authentication에는 토큰이 저장되어 있다.
     * 토큰을 사용하여 유저의 아이디와 권한을 가지고 오고
     * JwtAuthenticationToken에 토큰, 유저, 권한을 담아 보내준다. (123은 다른 것도 담을 수 있기에 예시로 넣어놨다.)
     *
     * @param authentication the authentication request object.
     * @return JwtAuthenticationToken (유저의 정보를 담아서 보내준다.)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = getToken(authentication);

        String username = jwtUtil.extractUsername(token);

        List<GrantedAuthority> authorities = extractUserGrade(token);

        return new JwtAuthenticationToken(token, username, "123", authorities);
    }

    /**
     * authentication 에서 토큰을 파싱해온다.
     *
     * @param authentication JwtFilter에서 전달받은 인증 객체
     * @return 파싱된 토큰
     */
    private String getToken(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        return jwtAuthenticationToken.getToken();
    }

    /**
     * 유저의 권한을 파싱해서 반환한다.
     *
     * @param token 파싱된 토큰
     * @return 유저의 권한
     */
    private List<GrantedAuthority> extractUserGrade(String token) {
        String userGrade = jwtUtil.extractUserGrade(token);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userGrade));

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}