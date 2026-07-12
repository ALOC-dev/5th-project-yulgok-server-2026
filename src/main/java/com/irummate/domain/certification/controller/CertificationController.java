package com.irummate.domain.certification.controller;

import com.irummate.domain.certification.dto.CertificationRequestDto;
import com.irummate.domain.certification.dto.CertificationResponseDto;
import com.irummate.domain.certification.service.CertificationService;
import com.irummate.global.response.GlobalApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public ResponseEntity<GlobalApiResponse<CertificationResponseDto>> submitCertification(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CertificationRequestDto requestDto
    ) {
        CertificationResponseDto responseDto = certificationService.submitCertification(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(HttpStatus.CREATED, "인증 요청이 제출되었습니다.", responseDto));
    }

    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<CertificationResponseDto>> getMyCertification(
            @AuthenticationPrincipal Long userId
    ) {
        CertificationResponseDto responseDto = certificationService.getMyCertification(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "내 인증 요청 조회 성공", responseDto)
        );
    }
}
