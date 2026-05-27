package com.dormmatch.domain.matching.dto;

import com.dormmatch.domain.matching.entity.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    String studentId;

    // 매칭 점수
    Long matchPercentage;

    // 매칭 상태
    MatchStatus matchStatus;


}
