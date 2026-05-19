package com.dormmatch.domain.survey.service;

import com.dormmatch.domain.survey.dto.UserPreferencesRequestDto;
import com.dormmatch.domain.survey.dto.UserPreferencesResponseDto;
import com.dormmatch.domain.survey.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    public SurveyService(UserPreferencesRepository userPreferencesRepository){
        this.userPreferencesRepository = userPreferencesRepository;
    }

    public UserPreferencesResponseDto getSurveyStatus(Long userId, UserPreferencesRequestDto requestDto){


        return new UserPreferencesResponseDto();
    }

}
