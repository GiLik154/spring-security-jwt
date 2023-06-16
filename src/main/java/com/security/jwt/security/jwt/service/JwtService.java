package com.security.jwt.security.jwt.service;

import com.security.jwt.domain.user.domain.User;
import com.security.jwt.domain.user.domain.UserRepository;
import com.security.jwt.security.exception.JwtTokenValidationException;
import com.security.jwt.security.jwt.dto.JwtTokenDto;
import com.security.jwt.security.jwt.repository.RefreshToken;
import com.security.jwt.security.jwt.repository.RefreshTokenRepository;
import com.security.jwt.security.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtService implements UserLogin, AccessTokenRefresher {
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 유저 로그인 시 처리되는 서비스.
     *
     * @param username 유저의 아이디
     * @param password 유저의 비밀번호
     * @return 토큰이 담긴 DTO
     */
    @Override
    public JwtTokenDto login(String username, String password) {
        User user = userRepository.findUserByUsername(username);

        validPassword(user, password);

        String accessToken = jwtUtil.generateAccessToken(username, user.getUserGrade().getAuthority());
        String refreshToken = generateRefreshToken(username);

        return new JwtTokenDto(accessToken, refreshToken);
    }

    /**
     * 유저의 비밀번호 비교
     *
     * @param user     비밀번호를 비교할 유저
     * @param password 유저의 입력 패스워드
     * @throws UsernameNotFoundException 유저의 비밀번호가 틀릴 시 전송
     */
    private void validPassword(User user, String password) {
        if (!user.matchPassword(passwordEncoder, password))
            throw new UsernameNotFoundException("Login Failed");
    }

    /**
     * 리프래쉬 토큰을 저장하는 메소드
     * 리프래쉬 토큰을 캐쉬로만 보내는 것 뿐 아니라 DB에도 전송해야 하기에
     * 메소드를 따로 빼 두었음.
     *
     * @param username 유저의 이름
     * @return 생성된 RefreshToken
     */
    private String generateRefreshToken(String username) {
        String token = jwtUtil.generateRefreshToken(username);

        RefreshToken refreshToken = tokenRepository.findByUsername(username)
                .orElseGet(() -> new RefreshToken(username));
        refreshToken.registerToken(token);

        return token;
    }

    /**
     * 토큰이 만료되었을 때 RefreshToken 을 이용해서 새로 발급 받는 메소드
     *
     * @param refreshToken 유저의 RefreshToken
     * @return 새로운 Access Token
     */
    @Override
    public String refresh(String refreshToken) {
        refreshToken = parsingToken(refreshToken);

        User user = validateRefreshToken(refreshToken);

        return jwtUtil.generateAccessToken(user.getName(), user.getUserGrade().getAuthority());
    }

    /**
     * 리프래쉬 토큰을 캐쉬에서 파싱하는 메소드
     *
     * @param refreshToken 캐쉬에서 얻어온 RefreshToken
     * @return 파싱된 RefreshToken
     * @throws JwtTokenValidationException 전송된 토큰의 양식이 일치하지 않을 시 발생
     */
    private String parsingToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) && !refreshToken.startsWith(BEARER))
            throw new JwtTokenValidationException("The form of the requested token is invalid.");

        return refreshToken.substring(7);
    }

    /**
     * 리프래쉬 토큰을 갱신하는 메소드
     * DB에 저장된 토큰과 일치하는지 확인함.
     *
     * @param refreshToken 파싱된 RefreshToken
     * @return RefreshToken에 담긴 User의 정보
     * @throws JwtTokenValidationException 전송된 토큰이 검증되지 않으면 발생
     */
    private User validateRefreshToken(String refreshToken) {
        String refreshTokenUsername = jwtUtil.extractUsername(refreshToken);

        if (!tokenRepository.existsByUsernameAndToken(refreshTokenUsername, refreshToken))
            throw new JwtTokenValidationException("This is not a normal token.");

        return userRepository.findUserByUsername(refreshTokenUsername);
    }
}
