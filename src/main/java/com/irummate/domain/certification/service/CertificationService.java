package com.irummate.domain.certification.service;

import com.irummate.domain.certification.dto.CertificationRequestDto;
import com.irummate.domain.certification.dto.CertificationResponseDto;
import com.irummate.domain.certification.dto.CertificationStatusResponseDto;
import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.entity.CertificationStatus;
import com.irummate.domain.certification.repository.CertificationRepository;
import com.irummate.domain.user.entity.UserRole;
import com.irummate.domain.user.entity.UserStatus;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UserDetailsRepository;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import com.irummate.global.util.HashIdsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final UsersRepository usersRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final HashIdsUtils hashIdsUtils;

    @Transactional
    public CertificationResponseDto createCertification(Long userId, CertificationRequestDto requestDto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        validateCertificationEligibility(userId, user);

        if (certificationRepository.existsByUser_IdAndSemester(userId, requestDto.getSemester())) {
            throw new BusinessException(ErrorCode.CERTIFICATION_ALREADY_EXISTS);
        }

        validateImageKey(userId, requestDto);

        Certification certification = Certification.builder()
                .user(user)
                .semester(requestDto.getSemester())
                .imageKey(requestDto.getImageKey())
                .certificationStatus(CertificationStatus.REQUESTED)
                .build();

        Certification savedCertification = certificationRepository.save(certification);
        return CertificationResponseDto.from(savedCertification,
                hashIdsUtils.encode(user.getId()),
                hashIdsUtils.encode(savedCertification.getId()));
    }

    public CertificationStatusResponseDto getLatestCertificationStatus(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Certification certification = certificationRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CERTIFICATION_NOT_FOUND));

        return CertificationStatusResponseDto.from(certification,
                hashIdsUtils.encode(user.getId()),
                hashIdsUtils.encode(certification.getId()));
    }

    private void validateCertificationEligibility(Long userId, Users user) {
        if (user.getRole() != UserRole.USER || user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "USER/PENDING 상태에서만 인증 요청이 가능합니다.");
        }

        if (!userDetailsRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_DETAILS_REQUIRED);
        }
    }

    private void validateImageKey(Long userId, CertificationRequestDto requestDto) {
        String expectedPrefix = "certifications/" + userId + "/" + requestDto.getSemester() + "/";

        if (!requestDto.getImageKey().startsWith(expectedPrefix)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }
}
