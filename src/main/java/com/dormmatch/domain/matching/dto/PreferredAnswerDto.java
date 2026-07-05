package com.dormmatch.domain.matching.dto;

import com.dormmatch.domain.survey.dto.SurveyAnswers;
import com.dormmatch.domain.survey.entity.SurveyAnswerField;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreferredAnswerDto {
    private SurveyAnswerField field;
    private Integer value;
}
