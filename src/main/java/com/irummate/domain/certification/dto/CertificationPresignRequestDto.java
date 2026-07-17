package com.irummate.domain.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CertificationPresignRequestDto {

    @NotBlank(message = "semester is required.")
    @Pattern(regexp = "^\\d{4}-[12]$", message = "semester must be yyyy-1 or yyyy-2.")
    private String semester;

    @NotBlank(message = "fileName is required.")
    private String fileName;

    @NotBlank(message = "contentType is required.")
    private String contentType;
}
