package com.dormmatch.domain.survey.util;

import com.dormmatch.domain.survey.dto.UserPreferencesRequestDto;
import com.dormmatch.domain.survey.dto.UserPreferencesResponseDto;
import com.dormmatch.domain.survey.entity.UserPreferences;
import com.dormmatch.domain.user.entity.Users;

public class UserPreferencesDtoMapper {

    public static UserPreferences toEntity(UserPreferencesRequestDto requestDto){
        return UserPreferences.builder()
                .activityTime(requestDto.getActivityTime())
                .bedTime(requestDto.getBedTime())
                .sleepSensitivity(requestDto.getSleepSensitivity())
                .lightSensitivity(requestDto.getLightSensitivity())
                .alarmHabit(requestDto.getAlarmHabit())
                .snoring(requestDto.getSnoring())
                .teethGrinding(requestDto.getTeethGrinding())
                .sleepTalking(requestDto.getSleepTalking())
                .cleaningFrequency(requestDto.getCleaningFrequency())
                .organizingStyle(requestDto.getOrganizingStyle())
                .eatingInRoom(requestDto.getEatingInRoom())
                .smokingStatus(requestDto.getSmokingStatus())
                .temperaturePreference(requestDto.getTemperaturePreference())
                .overnightAbsence(requestDto.getOvernightAbsence())
                .showerFrequency(requestDto.getShowerFrequency())
                .ventilationPreference(requestDto.getVentilationPreference())
                .scentSensitivity(requestDto.getScentSensitivity())
                .keyboardStyle(requestDto.getKeyboardStyle())
                .speakerStyle(requestDto.getSpeakerStyle())
                .callInRoom(requestDto.getCallInRoom())
                .roommateCloseness(requestDto.getRoommateCloseness())
                .conflictStyle(requestDto.getConflictStyle())
                .sharingAttitude(requestDto.getSharingAttitude())
                .studyLocation(requestDto.getStudyLocation())
                .build();
    }

}
