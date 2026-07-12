package com.irummate.domain.admin.service;

import com.irummate.domain.admin.dto.AdminCertificationRejectRequestDto;
import com.irummate.domain.certification.dto.CertificationResponseDto;
import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.entity.CertificationStatus;
import com.irummate.domain.certification.repository.CertificationRepository;
import com.irummate.domain.user.entity.Users;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
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
