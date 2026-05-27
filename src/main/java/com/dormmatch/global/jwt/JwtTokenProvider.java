package com.dormmatch.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    void init() {
        // jwt에서 HMAC 키를 만들 수 있도록 secret 값은 Base64 인코딩 문자열이어야 한다.
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    public String createAccessToken(Long userId, String role) {
        return createToken(userId, role, jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(Long userId, String role) {
        return createToken(userId, role, jwtProperties.getRefreshTokenExpiration());
    }

    public boolean validateToken(String token) {
        try {
            // 파싱 과정에서 서명과 만료 시간이 함께 검증된다.
            parseClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    private String createToken(Long userId, String role, Long expirationMillis) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMillis);

        // subject에는 로컬 사용자 ID를 저장하고, role은 권한 확인용 claim으로 저장한다.
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }
}
