package com.dormmatch.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private final Long userId;
    private final String tokenType;
    private final String accessToken;
    //private final String refreshToken;
    private final Long accessTokenExpiresIn;
    //private final Long refreshTokenExpiresIn;
}
