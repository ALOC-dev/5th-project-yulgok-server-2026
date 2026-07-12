package com.irummate.domain.certification.entity;

import com.irummate.domain.user.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "certifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificationStatus status = CertificationStatus.PENDING;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Certification(Users user, String imageUrl) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.status = CertificationStatus.PENDING;
    }

    public static Certification create(Users user, String imageUrl) {
        return new Certification(user, imageUrl);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isProcessed() {
        return status != CertificationStatus.PENDING;
    }

    public void approve() {
        this.status = CertificationStatus.APPROVED;
        this.adminComment = null;
    }

    public void reject(String adminComment) {
        this.status = CertificationStatus.REJECTED;
        this.adminComment = adminComment;
    }
}
