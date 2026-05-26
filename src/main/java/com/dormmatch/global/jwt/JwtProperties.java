package com.dormmatch.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
// application.yml 등에서 'jwt:'로 시작하는 설정(예: jwt.secret, jwt.access-token-expiration)을 매핑
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;                  // JWT 서명에 사용할 비밀키 (Base64 인코딩된 문자열)
    private Long accessTokenExpiration;    // Access Token 만료 시간 (밀리초 단위)
    private Long refreshTokenExpiration;   // Refresh Token 만료 시간 (밀리초 단위)
}
