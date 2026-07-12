package com.irummate.domain.survey.entity;

import com.irummate.domain.survey.dto.SurveyAnswers;
import com.irummate.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // ?섎뱶 肄붾뵫???ㅻЦ
    @Column(name = "smoking_status", nullable = false)
    private Integer smokingStatus;

    // ?먭린 ?뚭컻
    @Column(columnDefinition = "TEXT")
    private String introduce;

    // ?щ윭 ?ㅻЦ
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers", columnDefinition = "jsonb", nullable = false)
    private SurveyAnswers answers;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "visible_profile_fields", columnDefinition = "jsonb")
    private List<SurveyAnswerField> visibleProfileFields = new ArrayList<>();

    @Builder.Default
    @Array(length = 9)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "lifestyle_vector", columnDefinition = "vector(9)")
    private float[] lifestyleVector = new float[9];

    @PreUpdate
    public void onUpdate() {
        convertToNormalizedVector();
        this.updatedAt = LocalDateTime.now();
    }

    private void convertToNormalizedVector() {
        this.lifestyleVector[0] = normalize(this.answers.getBedtime(), 1, 5);
        this.lifestyleVector[1] = normalize(this.answers.getSnoring(), 1, 5);
        this.lifestyleVector[2] = normalize(this.answers.getSleepTalking(), 1, 5);
        this.lifestyleVector[3] = normalize(this.answers.getOrganizingStyle(), 1, 5);
        this.lifestyleVector[4] = normalize(this.answers.getTemperaturePreference(), 1, 3);
        this.lifestyleVector[5] = normalize(this.answers.getShowerFrequency(), 1 ,4);
        this.lifestyleVector[6] = normalize(this.answers.getSpeakerStyle(), 1, 3);
        this.lifestyleVector[7] = normalize(this.answers.getCallInRoom(), 1, 3);
        this.lifestyleVector[8] = normalize(this.answers.getEatingInRoom(), 1, 3);
    }

    private float normalize(Integer value, int min, int max){

        if(value == null)   return 0.0f;

        if(max == min)  return 0.0f;

        if(value < min) value = min;
        if(value > max) value = max;

        return ((float) value - min)/((float) max - min);
    }



    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;


    @Builder.Default
    @Column(name = "is_matched", nullable = false)
    private Boolean isMatched = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "rerolled_at")
    private LocalDateTime rerolledAt;

    @PrePersist
    public void onCreate() {
        convertToNormalizedVector();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateIsRerolled() {this.rerolledAt = LocalDateTime.now();}

    public void updateIsMatched() {
        this.isMatched = true;
    }

}
