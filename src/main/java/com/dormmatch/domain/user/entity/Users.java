package com.dormmatch.domain.user.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false)
    private String publicId;

    @Column(nullable = false, unique = true)
    private String oauthId;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Column(nullable = false)
    private String role = "GUEST"; // GUEST, USER, ADMIN

    @Column(nullable = false)
    private String status = "PENDING"; // ACTIVE, PENDING, BANNED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDetails userDetails;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(this.publicId == null) {
            this.publicId = UlidCreator.getUlid().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Users(String oauthId, String email, String nickname, String profileImageUrl) {
        this.oauthId          = oauthId;
        this.email            = email;
        this.nickname         = nickname;
        this.profileImageUrl  = profileImageUrl;
    }

    public void activate()  { this.status = "ACTIVE"; }
    public void ban()       { this.status = "BANNED"; }
    public void promoteToUser() { this.role = "USER"; }
}
