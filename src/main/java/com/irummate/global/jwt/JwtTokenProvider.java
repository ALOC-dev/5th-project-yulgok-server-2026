package com.irummate.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
        //this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes()
        );
    }
    public String createAccessToken(String userId, String role) {
        return createToken(userId, role, jwtProperties.getAccessTokenExpiration());
    }
    public String createRefreshToken(String userId, String role) {
        return createToken(userId, role, jwtProperties.getRefreshTokenExpiration());
    }
    public boolean validateToken(String token) {
        try {
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
    private String createToken(String userId, String role, Long expirationMillis) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }
    /*
    JWT payload
    {
    "sub": "1",
    "role": "USER",
    "iat": 1710000000,
    "exp": 1710003600
    }
     */
}
