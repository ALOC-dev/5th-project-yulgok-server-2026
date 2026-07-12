package com.dormmatch.domain.admin.service;

import com.dormmatch.domain.admin.dto.AdminCertificationRejectRequestDto;
import com.dormmatch.domain.certification.dto.CertificationResponseDto;
import com.dormmatch.domain.certification.entity.Certification;
import com.dormmatch.domain.certification.entity.CertificationStatus;
import com.dormmatch.domain.certification.repository.CertificationRepository;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCertificationService {

    private final CertificationRepository certificationRepository;

    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getCertifications(String status) {
        List<Certification> certifications = status == null || status.isBlank()
                ? certificationRepository.findAllByOrderByCreatedAtDesc()
                : certificationRepository.findAllByStatusOrderByCreatedAtDesc(parseStatus(status));

        return certifications.stream()
                .map(CertificationResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificationResponseDto getCertification(Long certificationId) {
        Certification certification = getCertificationEntity(certificationId);

        return CertificationResponseDto.from(certification);
    }

    @Transactional
    public CertificationResponseDto approveCertification(Long certificationId) {
        Certification certification = getPendingCertification(certificationId);

        certification.approve();

        // 인증 승인 시 매칭/서비스 이용이 가능하도록 유저 상태도 함께 갱신한다.
        Users user = certification.getUser();
        user.activate();
        if (!user.isAdmin()) {
            user.promoteToUser();
        }

        return CertificationResponseDto.from(certification);
    }

    @Transactional
    public CertificationResponseDto rejectCertification(
            Long certificationId,
            AdminCertificationRejectRequestDto requestDto
    ) {
        Certification certification = getPendingCertification(certificationId);

        certification.reject(requestDto.getAdminComment().trim());

        return CertificationResponseDto.from(certification);
    }

    private Certification getPendingCertification(Long certificationId) {
        Certification certification = getCertificationEntity(certificationId);

        if (certification.isProcessed()) {
            throw new BusinessException(ErrorCode.CERTIFICATION_ALREADY_PROCESSED);
        }

        return certification;
    }

    private Certification getCertificationEntity(Long certificationId) {
        return certificationRepository.findById(certificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_NOT_FOUND));
    }

    private CertificationStatus parseStatus(String status) {
        try {
            return CertificationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
