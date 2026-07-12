package com.irummate.domain.auth.controller;

import com.irummate.domain.auth.dto.AuthStatusResponseDto;
import com.irummate.domain.auth.dto.LoginResponseDto;
import com.irummate.domain.auth.dto.RefreshTokenResponseDto;
import com.irummate.domain.auth.service.AuthService;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import com.irummate.global.response.GlobalApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<GlobalApiResponse<?>> kakaoCallback(
            @RequestParam(value = "code", required = false) String code,
            HttpServletResponse servletResponse
    ) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "인가 코드가 필요합니다.");
        }

        AuthService.LoginResult loginResult = authService.loginOrRegister(code);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResult.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .sameSite("Lax")
                .build();

        servletResponse.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "Access Token 반환 성공", loginResult.response()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<GlobalApiResponse<RefreshTokenResponseDto>> refresh(
            HttpServletRequest request
    ) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh Token이 만료되었거나 유효하지 않습니다.");
        }

        RefreshTokenResponseDto response = authService.refreshAccessToken(refreshToken);

        if (response == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh Token이 만료되었거나 유효하지 않습니다.");
        }

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "Access Token 재발급 성공", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalApiResponse<Void>> logout(HttpServletResponse response) {
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", clearCookie.toString());
        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "로그아웃되었습니다.", null));
    }

    @GetMapping("/status")
    public ResponseEntity<GlobalApiResponse<AuthStatusResponseDto>> status() {
        AuthStatusResponseDto response = authService.getCurrentUserStatus();
        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "유저 정보 반환 성공", response));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
