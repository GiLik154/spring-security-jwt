package com.security.jwt;

import com.security.jwt.security.jwt.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
    public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Spring Security에 대한 기본적인 설정을 정의하는 메서드입니다.
     * 이 메서드는 SecurityFilterChain을 생성하여 반환하는 Bean으로 등록됩니다.
     *
     * @param http HttpSecurity 객체, Spring Security 설정을 구성하기 위한 메서드가 제공됩니다.
     * @return SecurityFilterChain 객체, Spring Security 필터 체인을 나타냅니다.
     * @throws Exception HttpSecurity 구성 중에 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
