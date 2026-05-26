package com.dormmatch.domain.survey.util;

import com.dormmatch.domain.survey.dto.UserPreferencesRequestDto;
import com.dormmatch.domain.survey.entity.UserPreferences;

public class UserPreferencesDtoMapper {

    public static UserPreferences toEntity(UserPreferencesRequestDto requestDto){
        return UserPreferences.builder()
                .smokingStatus(requestDto.getSmokingStatus())
                .introduce(requestDto.getIntroduce())
                .answers(requestDto.getAnswers())
                .build();
    }

}
