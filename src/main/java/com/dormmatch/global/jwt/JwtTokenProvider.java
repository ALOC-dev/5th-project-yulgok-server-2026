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
    private SecretKey secretKey; // jjwt에서 사용하는 암호화 키 객체

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 의존성 주입이 완료된 후, 암호화 키를 초기화하는 메서드
     */
    @PostConstruct
    void init() {
        // 주입받은 Base64 비밀키 문자열을 디코딩하여 HMAC-SHA 알고리즘에 적합한 SecretKey 객체로 변환합니다.
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    /**
     * Access Token을 생성합니다. (상대적으로 만료 시간이 짧음)
     */
    public String createAccessToken(Long userId, String role) {
        return createToken(userId, role, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Refresh Token을 생성합니다. (주로 Access Token 재발급용, 만료 시간이 김)
     */
    public String createRefreshToken(Long userId, String role) {
        return createToken(userId, role, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * 토큰의 유효성(서명 위조 여부, 만료 여부 등)을 검증합니다.
     */
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하는 과정에서 만료일(Expiration)이나 서명(Signature)이 잘못되면 예외가 발생합니다.
            parseClaims(token);
            return true; // 예외가 발생하지 않으면 유효한 토큰
        } catch (RuntimeException e) {
            // 실무에서는 이곳에서 MalformedJwtException, ExpiredJwtException 등 세부 예외 처리를 하기도 합니다.
            return false;
        }
    }

    /**
     * 토큰을 복호화하여 내부 데이터(Claims)를 추출합니다.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 검증에 사용할 비밀키 설정
                .build()
                .parseSignedClaims(token) // 서명된 토큰 파싱
                .getPayload(); // 내부 데이터(Claims) 반환
    }

    public Long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    /**
     * JWT 토큰을 실제로 빌드하는 공통 메서드
     */
    private String createToken(Long userId, String role, Long expirationMillis) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMillis); // 만료 시각 계산

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 대상을 식별하는 subject에 서비스 유저 ID 저장
                .claim("role", role)             // 커스텀 클레임으로 사용자의 권한(Role) 저장
                .issuedAt(now)                   // 토큰 발행 시간
                .expiration(expiresAt)           // 토큰 만료 시간
                .signWith(secretKey)             // 비밀키와 알고리즘으로 서명 생성
                .compact();                      // 직렬화하여 문자열로 변환
    }
}