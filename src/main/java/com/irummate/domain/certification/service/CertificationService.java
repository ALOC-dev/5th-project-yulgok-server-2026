package com.irummate.domain.certification.service;

import com.irummate.domain.certification.dto.CertificationRequestDto;
import com.irummate.domain.certification.dto.CertificationResponseDto;
import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.entity.CertificationStatus;
import com.irummate.domain.certification.repository.CertificationRepository;
import com.irummate.domain.user.entity.UserRole;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public CertificationResponseDto submitCertification(Long userId, CertificationRequestDto requestDto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.isAdmin()) {
            throw new BusinessException(ErrorCode.ADMIN_CERTIFICATION_NOT_ALLOWED);
        }

        if (user.getRole() != UserRole.USER) {
            throw new BusinessException(ErrorCode.USER_DETAILS_REQUIRED);
        }

        boolean hasActiveRequest = certificationRepository.existsByUserIdAndStatusIn(
                userId,
                List.of(CertificationStatus.PENDING, CertificationStatus.APPROVED)
        );

        if (hasActiveRequest) {
            throw new BusinessException(ErrorCode.CERTIFICATION_ALREADY_EXISTS);
        }

        Certification certification = Certification.create(user, requestDto.getImageUrl().trim());
        Certification savedCertification = certificationRepository.save(certification);

        return CertificationResponseDto.from(savedCertification);
    }

    @Transactional(readOnly = true)
    public CertificationResponseDto getMyCertification(Long userId) {
        Certification certification = certificationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_NOT_FOUND));

        return CertificationResponseDto.from(certification);
    }
}
