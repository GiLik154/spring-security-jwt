package com.security.jwt.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    /**
     * jwt의 시크릿 키
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Access Token의 유지 시간
     */
    @Value("${jwt.accessTokenExpiration}")
    private Long accessTokenExpiration;

    /**
     * Refresh Token의 유지 시간
     */
    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    /**
     * Access Token을 생성하는 메소드
     * Claims을 통해서 토큰에 담길 정보들을 담는다.
     * 이 곳에서는 유저의 아이디와 유저의 권한을 담고 있다.
     *
     * @param username  유저의 아이디
     * @param userGrade 유저의 권한
     * @return 생성된 Access Token
     */
    public String generateAccessToken(String username, String userGrade) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userGrade", userGrade);

        return createToken(claims, accessTokenExpiration);
    }

    /**
     * Refresh Token을 생성하는 메소드
     * Claims을 통해서 토큰에 담길 정보들을 담는다.
     * 이 곳에서는 유저의 아이디을 담고 있다.
     *
     * @param username 유저의 아이디
     * @return 생성된 Refresh Token
     */
    public String generateRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

        return createToken(claims, refreshTokenExpiration);
    }

    /**
     * 토큰을 생성하는 메소드
     * <p>
     * Jwts.builder() 을 통해서 토큰을 생성하게 된다.
     * setClaims(claims)을 통해서 토큰에 담을 정보들을 넣어준다
     * setSubject(subject) 을 통해서 JWT의 이름을 넣어준다. (없을 경우 Null)
     * setIssuedAt(now) 을 통해 발급 시간을 넣어준다.
     * setExpiration(expiryDate)을 통해 만료 시간을 넣어준다.
     * signWith(SignatureAlgorithm.HS512, secret) 을 통해 어떻게 암호화 할 것이지에 대해 정해준다.
     *
     * @param claims         토큰에 담길 정보들
     * @param expirationTime 만료시간
     * @return 생성된 토큰
     */
    private String createToken(Claims claims, Long expirationTime) {
        String subject = claims.getSubject();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 유저의 아이디를 추출하는 메소드
     *
     * @param token 추출할 토큰
     * @return 유저의 아이디
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 유저의 권한을 추출하는 메소드
     *
     * @param token 추출할 토큰
     * @return 유저의 권한
     */
    public String extractUserGrade(String token) {
        return extractClaim(token, claims -> claims.get("userGrade", String.class));
    }

    /**
     * 추출한 타입을 형변환 해주는 메소드
     *
     * @param token          추출할 토큰
     * @param claimsResolver 추출할 정보와 타입
     * @return 추출한 정보
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * 토큰을 복호화 해주는 메소드
     *
     * @param token 토큰
     * @return 복호화 된 Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}