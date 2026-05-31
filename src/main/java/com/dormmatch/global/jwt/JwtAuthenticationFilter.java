package com.dormmatch.global.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 요청 헤더에서 JWT access token을 꺼낸다.
        // 예: Authorization: Bearer eyJ...
        String token = resolveToken(request);

        // 토큰이 존재하고, 서명/만료시간 검증이 통과하면 인증 처리한다.
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // JWT payload 안에 들어있는 정보들을 꺼낸다.
            Claims claims = jwtTokenProvider.parseClaims(token);

            // JWT의 subject는 문자열로 저장되어 있음.
            // 하지만 우리 서비스 내부 users.id 타입은 Long이므로 Long으로 변환한다.
            Long userId = Long.valueOf(claims.getSubject());

            // JWT에 claim으로 저장해 둔 role 값을 꺼낸다.
            // 예: GUEST, USER, ADMIN
            String role = claims.get("role", String.class);

            // Spring Security가 이해할 수 있는 인증 객체를 만든다.
            // 첫 번째 인자 principal에 현재 로그인한 유저의 내부 id를 넣는다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // 현재 요청의 SecurityContext에 인증 정보를 저장한다.
            // 이후 컨트롤러/서비스/AOP에서 현재 로그인 유저 정보를 꺼낼 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청을 넘긴다.
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        // Authorization 헤더 값을 읽는다.
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        // Bearer 로 시작하면 실제 토큰 부분만 잘라서 반환한다.
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        // 토큰이 없으면 null 반환
        return null;
    }
}