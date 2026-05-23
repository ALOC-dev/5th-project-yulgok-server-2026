package com.dormmatch.domain.survey.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPreferencesRequestDto {

    // --- 수면 패턴 ---
    @NotNull(message = "activityTime은 필수입니다.")
    private Integer activityTime;

    @NotNull(message = "bedTime은 필수입니다.")
    private Integer bedTime;

    @NotNull(message = "sleepSensitivity는 필수입니다.")
    private Integer sleepSensitivity;

    @NotNull(message = "lightSensitivity는 필수입니다.")
    private Integer lightSensitivity;

    @NotNull(message = "alarmHabit은 필수입니다.")
    private Integer alarmHabit;

    @NotNull(message = "snoring은 필수입니다.")
    private Integer snoring;

    @NotNull(message = "teethGrinding은 필수입니다.")
    private Integer teethGrinding;

    @NotNull(message = "sleepTalking은 필수입니다.")
    private Integer sleepTalking;


    // --- 생활 습관 ---
    @NotNull(message = "cleaningFrequency는 필수입니다.")
    private Integer cleaningFrequency;

    @NotNull(message = "organizingStyle은 필수입니다.")
    private Integer organizingStyle;

    @NotNull(message = "eatingInRoom은 필수입니다.")
    private Integer eatingInRoom;

    @NotNull(message = "smokingStatus는 필수입니다.")
    private Integer smokingStatus;

    @NotNull(message = "temperaturePreference는 필수입니다.")
    private Integer temperaturePreference;

    @NotNull(message = "overnightAbsence는 필수입니다.")
    private Integer overnightAbsence;

    @NotNull(message = "showerFrequency는 필수입니다.")
    private Integer showerFrequency;


    // --- 환경·감각 ---
    @NotNull(message = "ventilationPreference는 필수입니다.")
    private Integer ventilationPreference;

    @NotNull(message = "scentSensitivity는 필수입니다.")
    private Integer scentSensitivity;


    // --- 소음 ---
    @NotNull(message = "keyboardStyle는 필수입니다.")
    private Integer keyboardStyle;

    @NotNull(message = "speakerStyle는 필수입니다.")
    private Integer speakerStyle;

    @NotNull(message = "callInRoom는 필수입니다.")
    private Integer callInRoom;


    // --- 관계·소통 ---
    @NotNull(message = "roommateCloseness는 필수입니다.")
    private Integer roommateCloseness;

    @NotNull(message = "conflictStyle는 필수입니다.")
    private Integer conflictStyle;

    @NotNull(message = "sharingAttitude는 필수입니다.")
    private Integer sharingAttitude;


    // --- 공부 ---
    @NotNull(message = "studyLocation는 필수입니다.")
    private Integer studyLocation;
}