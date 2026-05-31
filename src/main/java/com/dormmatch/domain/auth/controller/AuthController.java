package com.dormmatch.domain.auth.controller;

import com.dormmatch.domain.auth.dto.LoginResponseDto;
import com.dormmatch.domain.auth.service.AuthService;
import com.dormmatch.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponse<LoginResponseDto>> kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse servletResponse
    ) {
        // AuthService에서 accessToken 응답 DTO와 refreshToken을 같이 받는다.
        AuthService.LoginResult loginResult = authService.loginOrRegister(code);

        // refreshToken을 HttpOnly 쿠키로 만든다.
        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                        "refreshToken",
                        loginResult.refreshToken()
                )
                // JavaScript에서 document.cookie로 읽을 수 없게 막는다.
                .httpOnly(true)

                // 로컬 개발환경은 보통 http라서 false.
                // 배포환경 HTTPS에서는 true로 바꿔야 한다.
                .secure(false)

                // 모든 경로에서 쿠키가 전달되도록 설정한다.
                .path("/")

                // refreshToken 쿠키 만료 시간. 현재는 14일.
                .maxAge(Duration.ofDays(14))

                // 로컬 개발에서는 Lax가 테스트하기 편하다.
                // 프론트/백엔드 도메인이 완전히 다르면 None + Secure(true)를 고려한다.
                .sameSite("Lax")

                .build();

        // 실제 응답 헤더에 Set-Cookie를 추가한다.
        servletResponse.addHeader(
                "Set-Cookie",
                refreshTokenCookie.toString()
        );

        // JSON 응답에는 accessToken만 들어간다.
        return ResponseEntity.ok(
                ApiResponse.success(loginResult.response())
        );
    }
}