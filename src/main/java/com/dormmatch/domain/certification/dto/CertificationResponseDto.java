package com.dormmatch.domain.certification.dto;

import com.dormmatch.domain.certification.entity.Certification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CertificationResponseDto {

    private Long certificationId;
    private Long userId;
    private String imageUrl;
    private String status;
    private String adminComment;
    private LocalDateTime createdAt;

    public static CertificationResponseDto from(Certification certification) {
        return new CertificationResponseDto(
                certification.getId(),
                certification.getUser().getId(),
                certification.getImageUrl(),
                certification.getStatus().name(),
                certification.getAdminComment(),
                certification.getCreatedAt()
        );
    }
}
