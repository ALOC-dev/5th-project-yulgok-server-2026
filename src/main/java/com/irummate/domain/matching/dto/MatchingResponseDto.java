package com.irummate.domain.matching.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDto {

    // ?몃? 怨듦컻??userId;
    private String userId;

    // ?숈깮 ?대쫫
    private String name;

    // ?깅퀎
    private String gender;

    // ?섏씠
    private Integer age;

    // ?먭린?뚭컻
    private String introduce;

    // ?숆낵, ?숇쾲
    private String department;

    private Double matchPercentage;

    private MatchCardStatus matchStatus;

    private LocalDateTime matchDate;

    List<PreferredAnswerDto> preferredAnswers;

}
