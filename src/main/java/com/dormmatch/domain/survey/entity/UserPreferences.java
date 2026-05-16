package com.dormmatch.domain.survey.entity;

import com.dormmatch.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_preferences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    // 수면 패턴

    // 1(아침형) ~ 5(올빼미형)
    @Column(name = "activity_time", nullable = false)
    private int activityTime;

    // 취침 시간: 1(22시이전), 2(22시~23시), 3(23시~24시), 4(24시~01시), 5(01시이후)
    @Column(nullable = false)
    private int bedTime;

    // 수면 민감도: 1(둔감)~5(매우예민)
    @Column(name = "sleep_sensitivity", nullable = false)
    private int sleepSensitivity;

    // 수면 중 빛 민감도: 1(불켜도잠)~5(완전암막필요)
    @Column(name = "light_sensitivity", nullable = false)
    private int lightSensitivity;

    // 알람 습관: 1(한번에 듣고 기상), 2(3~4개 듣고 기상), 3(엄청 많이 들어야 기상)
    @Column(name = "alarm_habit", nullable = false)
    private int alarmHabit;

    // 코골이: true or false
    @Column(name = "has_snoring", nullable = false)
    private boolean hasSnoring;

    // 이갈이: true or false
    @Column(name = "has_teeth_grinding", nullable = false)
    private boolean hasTeethGrinding;


    // 생활 습관

    // 청소 빈도: 1(매일)~5(거의안함)
    @Column(name = "cleaning_frequency", nullable = false)
    private int cleaningFrequency;

    // 주변 정리: 1(매우깔끔)~5(어질러도괜찮음)
    @Column(name = "organizing_style", nullable = false)
    private int organizingStyle;

    // 방 안 음식 섭취: 1(YES), 2(SOMETIMES), 3(NO)
    @Column(name = "eating_in_room", nullable = false)
    private int eatingInRoom;

    // 흡연 여부: 1(SMOKER), 2(NON_SMOKER)
    @Column(name = "smoking_status", nullable = false)
    private int smokingStatus;

    // 온도 선호: 1(COLD), 2(MODERATE), 3(WARM)
    @Column(name = "temperature_preference", nullable = false)
    private int temperaturePreference;

    // 외박 빈도: 1(RARELY), 2(MONTHLY), 3(BIWEEKLY), 4(WEEKLY)
    @Column(name = "overnight_absence", nullable = false)
    private int overnightAbsence;

    // 샤워 빈도: 1(하루2회이상), 2(하루1회), 3(이틀에1회), 4(주몇회)
    @Column(name = "shower_frequency", nullable = false)
    private int showerFrequency;


    // 환경·감각

    // 환기 선호: 1(ALWAYS_OPEN), 2(PREFER_OPEN), 3(PREFER_CLOSED), 4(ALWAYS_CLOSED)
    @Column(name = "ventilation_preference", nullable = false)
    private int ventilationPreference;

    // 향 민감도: 1(안예민)~5(매우예민)
    @Column(name = "scent_sensitivity", nullable = false)
    private int scentSensitivity;


    // 소음

    // 키보드 소음: 1(무소음)~5(소리큰편)
    @Column(name = "keyboard_style", nullable = false)
    private int keyboardStyle;

    // 스피커 사용: 1(EARPHONE), 2(SPEAKER), 3(BOTH)
    @Column(name = "speaker_style", nullable = false)
    private int speakerStyle;

    // 방 안 통화: 1(YES), 2(EARPHONE_ONLY), 3(NO)
    @Column(name = "call_in_room", nullable = false)
    private int callInRoom;


    // 관계·소통

    // 룸메이트 친밀도: 1(CLOSE), 2(MODERATE), 3(INDEPENDENT)
    @Column(name = "roommate_closeness", nullable = false)
    private int roommateCloseness;

    // 갈등 해결 방식: 1(DIRECT), 2(INDIRECT), 3(AVOID)
    @Column(name = "conflict_style", nullable = false)
    private int conflictStyle;

    // 물건 공유: 1(OPEN), 2(ASK_FIRST), 3(PREFER_SEPARATE)
    @Column(name = "sharing_attitude", nullable = false)
    private int sharingAttitude;


    // 공부

    // 공부 장소: 1(IN_ROOM), 2(OUTSIDE), 3(BOTH)
    @Column(name = "study_location", nullable = false)
    private int studyLocation;


    // 메타

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
