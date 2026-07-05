package com.dormmatch.domain.matching.dto;

import com.dormmatch.domain.matching.entity.MatchStatus;
import com.dormmatch.domain.survey.entity.SurveyAnswerField;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDto {

    // 외부 공개용 userId;
    private Long userId;

    // 학생 이름
    private String name;

    // 성별
    private String gender;

    // 나이
    private Integer age;

    // 자기소개
    private String introduce;

    // 학과, 학번
    private String department;

    // 매칭 점수
    private Double matchPercentage;

    //매칭 상태
    private MatchCardStatus matchStatus;


    // 요청자 선택 응답
    List<PreferredAnswerDto> preferredAnswers;

}
