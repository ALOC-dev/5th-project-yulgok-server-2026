package com.irummate.domain.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CertificationRequestDto {

    @NotBlank(message = "semester is required.")
    @Pattern(regexp = "^\\d{4}-[12]$", message = "semester must be yyyy-1 or yyyy-2.")
    private String semester;

    @NotBlank(message = "imageKey is required.")
    private String imageKey;
}
