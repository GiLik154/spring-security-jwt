package com.security.jwt.security.jwt.filter;

import com.security.jwt.security.jwt.provider.JwtProvider;
import com.security.jwt.security.jwt.token.JwtAuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    /**
     * 토큰을 사용하지 않는 경우는 필터 적용을 피하기 위해서
     */
    private static final String INFO_URI = "/info";
    /**
     * 클라이언트가 보내주는 헤더의 이름
     */
    private static final String AUTHORIZATION = "Authorization";
    /**
     * 토큰의 타입
     */
    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    /**
     * 토큰에서 유저의 정보를 빼내어 인증 객체로 추출하여 보내주는 로직.
     * <p>
     * HttpServletRequest의 헤더에서 토큰을 가지고 와서 사용할 수 있게 파싱한다.
     * 이후 토큰을 담아서 JwtProvider로 보내고, 사용이 가능한지 검증 및 유저의 정보를 파싱해서 담아온다.
     * SecurityContextHolder 를 통해 컨트롤러로 보내준다.
     * <p>
     * 만약 {@link ExpiredJwtException} 이 터진다면, 만료된 토큰이므로 403롤 반환한다.
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param filterChain FilterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //토큰을 가지고 온다
        String token = resolveToken(request);

        //현재 접속한 URI을 가지고 온다.
        String url = request.getRequestURI();

        // 토큰이 null이 아니고, /info가 아니면 굳이 로직을 실행할 이유가 없으니 필터를 패스한다.
        if (StringUtils.hasText(token) && url.equals(INFO_URI)) {
            try {
                //Authentication에 토큰을 담는다.
                Authentication jwtAuthenticationToken = new JwtAuthenticationToken(token);
                //JwtProvider에 Authentication을 담아서 보내고, 검증 후에 Authentication를 다시 반환한다.
                Authentication authentication = jwtProvider.authenticate(jwtAuthenticationToken);

                //SecurityContextHolder에 Authentication 담는다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException expiredJwtException) {
                //토큰이 만료될 겨우 403을 반환한다.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Token expired");
                response.getWriter().flush();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        //헤더에서 토큰을 가지고 온다.
        String bearerToken = request.getHeader(AUTHORIZATION);

        //토큰의 양식이 사용 가능한지 확인한다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(7);
        }

        return null;
    }
}