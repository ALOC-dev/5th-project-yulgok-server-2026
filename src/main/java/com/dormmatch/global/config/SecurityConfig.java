package com.dormmatch.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 차단 해제 (API 서버니까)
                .formLogin(form -> form.disable()) // 중요: 스프링 기본 로그인 폼 화면 꺼버리기
                .httpBasic(basic -> basic.disable()) // 기본 HTTP 로그인창 꺼버리기
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 임시로 모두 허용 후 나중에 JWT 필터 연결
                );

        return http.build();
    }
}
