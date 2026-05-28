package com.dormmatch.domain.matching.dto;

import com.dormmatch.domain.matching.entity.MatchStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDto {

    // 외부 공개용 userId;
    String userId;

    // 학생 이름
    String name;

    // 성별
    String gender;

    // 나이
    Integer age;

    // 자기소개
    String introduce;

    // 학과, 학번
    String department;

    // 매칭 점수
    Double matchPercentage;

    // 매칭 상태
    MatchStatus matchStatus;

}
