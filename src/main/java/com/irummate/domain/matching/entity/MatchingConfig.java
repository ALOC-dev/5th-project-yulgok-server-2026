package com.irummate.domain.matching.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "matching_config")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingConfig {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @Column(name = "config_id")
    private Long id = SINGLETON_ID;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "updated_at", nullable = false)
    private  LocalDateTime updatedAt;

    public MatchingConfig(LocalDate matchDate){
        this.id = SINGLETON_ID;
        this.matchDate = matchDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
        this.updatedAt = LocalDateTime.now();
    }

}
