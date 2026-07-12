package com.irummate.domain.certification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CertificationRequestDto {

    @NotBlank(message = "인증 이미지 URL은 필수입니다.")
    private String imageUrl;
}
