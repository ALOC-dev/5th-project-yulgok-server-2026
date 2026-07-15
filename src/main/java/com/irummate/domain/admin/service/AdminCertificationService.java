package com.irummate.domain.admin.service;

import com.irummate.domain.admin.dto.AdminCertificationRejectRequestDto;
import com.irummate.domain.admin.dto.AdminCertificationResponseDto;
import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.entity.CertificationStatus;
import com.irummate.domain.certification.repository.CertificationRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import com.irummate.global.util.HashIdsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCertificationService {

    private final CertificationRepository certificationRepository;

    public List<AdminCertificationResponseDto> getCertifications(CertificationStatus status) {
        List<Certification> certifications = (status == null)
                ? certificationRepository.findAllByOrderByCreatedAtDesc()
                : certificationRepository.findAllByCertificationStatusOrderByCreatedAtDesc(status);

        return certifications.stream()
                .map(AdminCertificationResponseDto::from)
                .toList();
    }

    public AdminCertificationResponseDto getCertification(String certificationId) {
        Certification certification = getCertificationEntity(certificationId);
        return AdminCertificationResponseDto.from(certification);
    }

    @Transactional
    public AdminCertificationResponseDto approveCertification(String certificationId) {
        Certification certification = getCertificationEntity(certificationId);
        ensurePending(certification);

        certification.approve(null);

        return AdminCertificationResponseDto.from(certification);
    }

    @Transactional
    public AdminCertificationResponseDto rejectCertification(String certificationId, AdminCertificationRejectRequestDto requestDto) {
        Certification certification = getCertificationEntity(certificationId);
        ensurePending(certification);

        certification.reject(requestDto.getAdminComment());

        return AdminCertificationResponseDto.from(certification);
    }

    private Certification getCertificationEntity(String certificationId) {
        Long decodedCertificationId;
        try {
            decodedCertificationId = HashIdsUtils.decode(certificationId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        return certificationRepository.findById(decodedCertificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_NOT_FOUND));
    }

    private void ensurePending(Certification certification) {
        if (certification.getCertificationStatus() != CertificationStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.CERTIFICATION_ALREADY_PROCESSED);
        }
    }
}
