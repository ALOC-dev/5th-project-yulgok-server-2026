package com.irummate.domain.certification.service;

import com.irummate.domain.certification.dto.CertificationPresignRequestDto;
import com.irummate.domain.certification.dto.CertificationPresignResponseDto;
import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.repository.CertificationRepository;
import com.irummate.domain.user.entity.UserRole;
import com.irummate.domain.user.entity.UserStatus;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UserDetailsRepository;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import com.irummate.global.s3.PresignedUrlResponse;
import com.irummate.global.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationPresignService {


    private final S3Utils s3Utils;
    private final CertificationRepository certificationRepository;


    public CertificationPresignResponseDto createUploadUrl(Long userId, CertificationPresignRequestDto requestDto) {

        String fileName = "Certification-"+requestDto.getSemester();

        PresignedUrlResponse presignedUrlResponse = s3Utils.createUploadUrl(requestDto.getFileName(), requestDto.getFileName(), fileName);

        return CertificationPresignResponseDto.builder()
                .uploadUrl(presignedUrlResponse.presignedUrl())
                .fileKey(presignedUrlResponse.key())
                .expiresAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5))
                .build();
    }

}
