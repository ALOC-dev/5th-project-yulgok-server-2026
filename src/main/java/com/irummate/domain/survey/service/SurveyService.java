package com.irummate.domain.survey.service;

import com.irummate.domain.survey.dto.SurveyAnswers;
import com.irummate.domain.survey.dto.UserPreferencesRequestDto;
import com.irummate.domain.survey.dto.UserPreferencesResponseDto;
import com.irummate.domain.survey.entity.UserPreferences;
import com.irummate.domain.survey.repository.UserPreferencesRepository;
import com.irummate.domain.survey.util.UserPreferencesDtoMapper;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyService {

    final private UsersRepository usersRepository;
    final private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    public SurveyService(UserPreferencesRepository userPreferencesRepository,
                         UsersRepository usersRepository){
        this.userPreferencesRepository = userPreferencesRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional(readOnly = true)
    public UserPreferencesResponseDto getSurveyStatus(Long userId){

        UserPreferences userPreferences = userPreferencesRepository.findByUserId(userId)
                .orElseThrow(()->new BusinessException(ErrorCode.SURVEY_NOT_FOUND));


        return UserPreferencesDtoMapper.toDto(userPreferences);
    }

    @Transactional
    public void saveSurveyStatus(Long userId, UserPreferencesRequestDto requestDto){

        Users user = usersRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(userPreferencesRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_SUBMITTED);
        }

        int size = requestDto.getVisibleProfileFields().size();
        long distinctSize = requestDto.getVisibleProfileFields().stream()
                .distinct()
                .count();

        if (size != distinctSize) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
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
                        .visibleProfileFields(requestDto.getVisibleProfileFields())
                        .build());
    }

}
