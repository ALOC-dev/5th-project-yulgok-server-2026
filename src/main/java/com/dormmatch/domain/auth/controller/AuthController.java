package com.dormmatch.domain.auth.controller;

import com.dormmatch.domain.auth.dto.LoginResponseDto;
import com.dormmatch.domain.auth.service.AuthService;
import com.dormmatch.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponse<LoginResponseDto>> kakaoCallback(@RequestParam("code") String code) {
        LoginResponseDto loginResponse = authService.loginOrRegister(code);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }
}
