package com.irummate.domain.admin.dto;

import com.irummate.domain.certification.entity.Certification;
import com.irummate.global.util.HashIdsUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminCertificationResponseDto {

    private String certificationId;
    private String userId;
    private String imageKey;
    private String status;
    private String adminComment;
    private LocalDateTime createdAt;

    public static AdminCertificationResponseDto from(Certification certification) {
        return new AdminCertificationResponseDto(
                HashIdsUtils.encode(certification.getId()),
                HashIdsUtils.encode(certification.getUser().getId()),
                certification.getImageKey(),
                certification.getCertificationStatus().name(),
                certification.getAdminComment(),
                certification.getCreatedAt()
        );
    }
}
