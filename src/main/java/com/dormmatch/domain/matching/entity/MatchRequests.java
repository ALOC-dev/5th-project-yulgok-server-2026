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
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Users receiver;

    @ManyToOne
    @JoinColumn(name = "receiver_preferences_id")
    private UserPreferences receiverPreferences;

    @ManyToOne
    @JoinColumn(name = "sender_preferences_id")
    private UserPreferences senderPreferences;

    @Column
    private Double matchPercentage;

    @Column(nullable = false)
    private MatchStatus status;

    @Column
    private LocalDateTime matchedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.matchedAt = LocalDateTime.now();
    }

    public void updateStatus(MatchStatus status) {
        this.status = status;
    }
}
