package com.irummate.domain.admin.controller;

import com.irummate.domain.admin.dto.AdminCertificationRejectRequestDto;
import com.irummate.domain.admin.service.AdminCertificationService;
import com.irummate.domain.certification.dto.CertificationResponseDto;
import com.irummate.global.aop.AuthRole;
import com.irummate.global.aop.RequiresAuth;
import com.irummate.global.response.GlobalApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/certifications")
public class AdminCertificationController {

    private final AdminCertificationService adminCertificationService;

    @GetMapping
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<List<CertificationResponseDto>>> getCertifications(
            @RequestParam(required = false) String status
    ) {
        List<CertificationResponseDto> responseDtos = adminCertificationService.getCertifications(status);

        return ResponseEntity.ok(
                GlobalApiResponse.multiSuccess(
                        HttpStatus.OK,
                        "인증 요청 목록 조회 성공",
                        responseDtos,
                        responseDtos.size()
                )
        );
    }

    @GetMapping("/{certificationId}")
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<CertificationResponseDto>> getCertification(
            @PathVariable Long certificationId
    ) {
        CertificationResponseDto responseDto = adminCertificationService.getCertification(certificationId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "인증 요청 상세 조회 성공", responseDto)
        );
    }

    @PatchMapping("/{certificationId}/approve")
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<CertificationResponseDto>> approveCertification(
            @PathVariable Long certificationId
    ) {
        CertificationResponseDto responseDto = adminCertificationService.approveCertification(certificationId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "인증 요청 승인 성공", responseDto)
        );
    }

    @PatchMapping("/{certificationId}/reject")
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<CertificationResponseDto>> rejectCertification(
            @PathVariable Long certificationId,
            @Valid @RequestBody AdminCertificationRejectRequestDto requestDto
    ) {
        CertificationResponseDto responseDto =
                adminCertificationService.rejectCertification(certificationId, requestDto);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "인증 요청 거절 성공", responseDto)
        );
    }
}
