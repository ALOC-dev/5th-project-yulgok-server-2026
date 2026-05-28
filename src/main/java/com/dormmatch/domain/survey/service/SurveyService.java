package com.dormmatch.domain.survey.service;

import com.dormmatch.domain.survey.dto.SurveyAnswers;
import com.dormmatch.domain.survey.dto.UserPreferencesRequestDto;
import com.dormmatch.domain.survey.dto.UserPreferencesResponseDto;
import com.dormmatch.domain.survey.entity.UserPreferences;
import com.dormmatch.domain.survey.repository.UserPreferencesRepository;
import com.dormmatch.domain.survey.util.UserPreferencesDtoMapper;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyService {

    private UsersRepository usersRepository;
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    public SurveyService(UserPreferencesRepository userPreferencesRepository,
                         UsersRepository usersRepository){
        this.userPreferencesRepository = userPreferencesRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public UserPreferencesResponseDto getSurveyStatus(Long userId){

        UserPreferences userPreferences = userPreferencesRepository.findByUserId(userId).get();

        return UserPreferencesDtoMapper.toDto(userPreferences);
    }

    @Transactional
    public void saveSurveyStatus(Long userId, UserPreferencesRequestDto requestDto){

        Users user = usersRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(userPreferencesRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_SUBMITTED);
        }

        SurveyAnswers surveyAnswers = SurveyAnswers.builder()
                        .bedtime(requestDto.getAnswers().getBedtime())
                        .snoring(requestDto.getAnswers().getSnoring())
                        .sleepTalking(requestDto.getAnswers().getSleepTalking())
                        .organizingStyle(requestDto.getAnswers().getOrganizingStyle())
                        .eatingInRoom(requestDto.getAnswers().getEatingInRoom())
                        .temperaturePreference(requestDto.getAnswers().getTemperaturePreference())
                        .showerFrequency(requestDto.getAnswers().getShowerFrequency())
                        .speakerStyle(requestDto.getAnswers().getSpeakerStyle())
                        .callInRoom(requestDto.getAnswers().getCallInRoom())
                        .build();

        userPreferencesRepository.save(UserPreferences.builder()
                        .introduce(requestDto.getIntroduce())
                        .answers(surveyAnswers)
                        .smokingStatus(requestDto.getSmokingStatus())
                        .user(user)
                        .isCompleted(true)
                        .isMatched(false)
                        .build());
    }

}
