package com.dormmatch.domain.survey.entity;

import com.dormmatch.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_preferences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    // 수면 패턴

    // 1(아침형) ~ 5(올빼미형)
    @Column(name = "activity_time", nullable = false)
    private Integer activityTime;

    // 취침 시간: 1(22시이전), 2(22시~23시), 3(23시~24시), 4(24시~01시), 5(01시이후)
    @Column(nullable = false)
    private Integer bedTime;

    // 수면 민감도: 1(둔감)~5(매우예민)
    @Column(name = "sleep_sensitivity", nullable = false)
    private Integer sleepSensitivity;

    // 수면 중 빛 민감도: 1(불켜도잠)~5(완전암막필요)
    @Column(name = "light_sensitivity", nullable = false)
    private Integer lightSensitivity;

    // 알람 습관: 1(한번에 듣고 기상), 2(3~4개 듣고 기상), 3(엄청 많이 들어야 기상)
    @Column(name = "alarm_habit", nullable = false)
    private Integer alarmHabit;

    // 코골이: 1~5
    @Column(name = "snoring", nullable = false)
    private Integer snoring;

    // 이갈이: 1~5
    @Column(name = "teeth_grinding", nullable = false)
    private Integer teethGrinding;

    // 잠꼬대: 1~5
    @Column(name = "sleepTalking", nullable = false)
    private Integer sleepTalking;


    // 생활 습관

    // 청소 빈도: 1(매일)~5(거의안함)
    @Column(name = "cleaning_frequency", nullable = false)
    private Integer cleaningFrequency;

    // 주변 정리: 1(매우깔끔)~5(어질러도괜찮음)
    @Column(name = "organizing_style", nullable = false)
    private Integer organizingStyle;

    // 방 안 음식 섭취: 1(YES), 2(SOMETIMES), 3(NO)
    @Column(name = "eating_in_room", nullable = false)
    private Integer eatingInRoom;

    // 흡연 여부: 1(SMOKER), 2(NON_SMOKER)
    @Column(name = "smoking_status", nullable = false)
    private Integer smokingStatus;

    // 온도 선호: 1(COLD), 2(MODERATE), 3(WARM)
    @Column(name = "temperature_preference", nullable = false)
    private Integer temperaturePreference;

    // 외박 빈도: 1(RARELY), 2(MONTHLY), 3(BIWEEKLY), 4(WEEKLY)
    @Column(name = "overnight_absence", nullable = false)
    private Integer overnightAbsence;

    // 샤워 빈도: 1(하루2회이상), 2(하루1회), 3(이틀에1회), 4(주몇회)
    @Column(name = "shower_frequency", nullable = false)
    private Integer showerFrequency;


    // 환경·감각

    // 환기 선호: 1(ALWAYS_OPEN), 2(PREFER_OPEN), 3(PREFER_CLOSED), 4(ALWAYS_CLOSED)
    @Column(name = "ventilation_preference", nullable = false)
    private Integer ventilationPreference;

    // 향 민감도: 1(안예민)~5(매우예민)
    @Column(name = "scent_sensitivity", nullable = false)
    private Integer scentSensitivity;


    // 소음

    // 키보드 소음: 1(무소음)~5(소리큰편)
    @Column(name = "keyboard_style", nullable = false)
    private Integer keyboardStyle;

    // 스피커 사용: 1(EARPHONE), 2(SPEAKER), 3(BOTH)
    @Column(name = "speaker_style", nullable = false)
    private Integer speakerStyle;

    // 방 안 통화: 1(YES), 2(EARPHONE_ONLY), 3(NO)
    @Column(name = "call_in_room", nullable = false)
    private Integer callInRoom;


    // 관계·소통

    // 룸메이트 친밀도: 1(CLOSE), 2(MODERATE), 3(INDEPENDENT)
    @Column(name = "roommate_closeness", nullable = false)
    private Integer roommateCloseness;

    // 갈등 해결 방식: 1(DIRECT), 2(INDIRECT), 3(AVOID)
    @Column(name = "conflict_style", nullable = false)
    private Integer conflictStyle;

    // 물건 공유: 1(OPEN), 2(ASK_FIRST), 3(PREFER_SEPARATE)
    @Column(name = "sharing_attitude", nullable = false)
    private Integer sharingAttitude;


    // 공부

    // 공부 장소: 1(IN_ROOM), 2(OUTSIDE), 3(BOTH)
    @Column(name = "study_location", nullable = false)
    private Integer studyLocation;



    // 정규화 벡터
    @Builder.Default
    @Column(name = "lifestyle_vector", columnDefinition = "vector(24)")
    private double[] lifestyleVector = new double[24];

    @PrePersist
    @PreUpdate
    public void onCreateOrUpdate() {
        convertToNormalizedVector();
    }

    private void convertToNormalizedVector() {

        this.lifestyleVector[0] = normalize(this.activityTime, 1, 5);
        this.lifestyleVector[1] = normalize(this.bedTime, 1, 5);
        this.lifestyleVector[2] = normalize(this.sleepSensitivity, 1, 5);
        this.lifestyleVector[3] = normalize(this.lightSensitivity, 1, 5);
        this.lifestyleVector[4] = normalize(this.alarmHabit, 1, 3);
        this.lifestyleVector[5] = normalize(this.snoring, 1 ,5);
        this.lifestyleVector[6] = normalize(this.teethGrinding, 1, 5);
        this.lifestyleVector[7] = normalize(this.sleepTalking, 1, 5);
        this.lifestyleVector[8] = normalize(this.cleaningFrequency, 1, 5);
        this.lifestyleVector[9] = normalize(this.organizingStyle, 1, 5);
        this.lifestyleVector[10] = normalize(this.eatingInRoom, 1, 3);
        this.lifestyleVector[11] = normalize(this.smokingStatus, 1, 2);
        this.lifestyleVector[12] = normalize(this.temperaturePreference, 1, 3);
        this.lifestyleVector[13] = normalize(this.overnightAbsence, 1, 4);
        this.lifestyleVector[14] = normalize(this.showerFrequency, 1, 4);
        this.lifestyleVector[15] = normalize(this.ventilationPreference, 1, 4);
        this.lifestyleVector[16] = normalize(this.scentSensitivity, 1, 5);
        this.lifestyleVector[17] = normalize(this.keyboardStyle, 1, 5);
        this.lifestyleVector[18] = normalize(this.speakerStyle, 1, 3);
        this.lifestyleVector[19] = normalize(this.callInRoom, 1, 3);
        this.lifestyleVector[20] = normalize(this.roommateCloseness, 1, 3);
        this.lifestyleVector[21] = normalize(this.conflictStyle, 1, 3);
        this.lifestyleVector[22] = normalize(this.sharingAttitude, 1, 3);
        this.lifestyleVector[23] = normalize(this.studyLocation, 1, 3);
    }

    private double normalize(Integer value, int min, int max){

        if(value == null)   return 0.0;

        if(max == min)  return 0.0;

        if(value < min) value = min;
        if(value > max) value = max;

        return ((double) value - min)/((double) max - min);
    }


    // 메타

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
