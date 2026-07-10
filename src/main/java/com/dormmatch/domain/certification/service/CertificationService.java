package com.dormmatch.domain.certification.service;

import com.dormmatch.domain.certification.dto.CertificationRequestDto;
import com.dormmatch.domain.certification.dto.CertificationResponseDto;
import com.dormmatch.domain.certification.entity.Certification;
import com.dormmatch.domain.certification.entity.CertificationStatus;
import com.dormmatch.domain.certification.repository.CertificationRepository;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
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
