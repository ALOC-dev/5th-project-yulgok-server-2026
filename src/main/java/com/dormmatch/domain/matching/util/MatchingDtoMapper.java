package com.dormmatch.domain.matching.util;

import com.dormmatch.domain.matching.dto.MatchingResponseDto;
import com.dormmatch.domain.matching.entity.MatchRequests;

public class MatchingDtoMapper {
    public static MatchingResponseDto toResponseDto(MatchRequests matchRequests) {
        return new MatchingResponseDto();
    }
}
