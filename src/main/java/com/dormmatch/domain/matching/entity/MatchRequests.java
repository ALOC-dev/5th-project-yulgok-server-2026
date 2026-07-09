package com.dormmatch.domain.matching.entity;

import com.dormmatch.domain.survey.entity.UserPreferences;
import com.dormmatch.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "match_requests")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_low_id")
    private Users userLow;

    @ManyToOne
    @JoinColumn(name = "user_high_id")
    private Users userHigh;

    @ManyToOne
    @JoinColumn(name = "user_low_preferences_id")
    private UserPreferences userLowPreferences;

    @ManyToOne
    @JoinColumn(name = "user_high_preferences_id")
    private UserPreferences userHighPreferences;

    @Column
    private Double matchPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_low_status")
    private MatchStatus userLowStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_high_status")
    private MatchStatus userHighStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_low_recommended_at")
    private LocalDateTime userLowRecommendedAt;

    @Column(name = "user_high_recommended_at")
    private LocalDateTime userHighRecommendedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public MatchStatus getStatusOf(Long userId) {
        if (userLow.getId().equals(userId)) {
            return userLowStatus;
        }
        if (userHigh.getId().equals(userId)) {
            return userHighStatus;
        }
        throw new IllegalArgumentException("User is not part of this match request.");
    }

    public void updateStatusOf(Long userId, MatchStatus status) {
        if (userLow.getId().equals(userId)) {
            this.userLowStatus = status;
            if (status == MatchStatus.RECOMMENDED) {
                this.userLowRecommendedAt = LocalDateTime.now();
            }
            return;
        }

        if (userHigh.getId().equals(userId)) {
            this.userHighStatus = status;
            if (status == MatchStatus.RECOMMENDED) {
                this.userHighRecommendedAt = LocalDateTime.now();
            }
            return;
        }

        throw new IllegalArgumentException("User is not part of this match request.");
    }

    public boolean isHeartMatched() {
        return userLowStatus == MatchStatus.HEART
                && userHighStatus == MatchStatus.HEART;
    }

    public boolean isConfirmed() {
        return userLowStatus == MatchStatus.FINAL_CONFIRMED
                && userHighStatus == MatchStatus.FINAL_CONFIRMED;
    }

    public boolean isRejected() {
        return userLowStatus == MatchStatus.REJECTED
                || userHighStatus == MatchStatus.REJECTED;
    }
}
