package com.irummate.domain.user.controller;

import com.irummate.domain.user.dto.UserDetailsRequestDto;
import com.irummate.domain.user.dto.UserDetailsResponseDto;
import com.irummate.domain.user.dto.UserProfileResponseDto;
import com.irummate.domain.user.dto.UserProfileUpdateRequestDto;
import com.irummate.domain.user.dto.UserProfileUpdateResponseDto;
import com.irummate.domain.user.service.UserDetailsService;
import com.irummate.global.response.GlobalApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<UserProfileResponseDto>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        UserProfileResponseDto response = userDetailsService.getProfile(userId);

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "응답 성공", response));
    }

    @PatchMapping("/me")
    public ResponseEntity<GlobalApiResponse<UserProfileUpdateResponseDto>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserProfileUpdateRequestDto request
    ) {
        UserProfileUpdateResponseDto response = userDetailsService.updateProfile(userId, request);

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK, "프로필이 수정되었습니다.", response));
    }

    @PostMapping("/details")
    public ResponseEntity<GlobalApiResponse<UserDetailsResponseDto>> createDetails(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserDetailsRequestDto request
    ) {
        UserDetailsResponseDto response = userDetailsService.createDetails(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(HttpStatus.CREATED, "필수 정보가 등록되었습니다.", response));
    }
}
