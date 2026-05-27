package com.dormmatch.domain.survey.repository;

import com.dormmatch.domain.survey.entity.UserPreferences;
import com.pgvector.PGvector;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUserId(Long userId);

    // 매칭 안 된 유저 전체 조회
    List<UserPreferences> findByIsMatchedFalseAndIsCompletedTrue();

    @Query(value = """
    SELECT up.user_id
    FROM user_preferences up
    JOIN users u ON up.user_id = u.id
    JOIN user_details ud ON up.user_id = ud.user_id
    WHERE u.status = 'ACTIVE'
      AND u.role = 'USER'
      AND up.is_completed = true
      AND up.is_matched = false
      AND up.smoking_status = :smokingStatus
      AND ud.gender = :gender
      AND up.user_id != :myUserId
    ORDER BY up.lifestyle_vector <-> :vector
    LIMIT 3
    """, nativeQuery = true)
    List<Long> findTop3MatchedUserIds(
            @Param("myUserId") Long myUserId,
            @Param("smokingStatus") Integer smokingStatus,
            @Param("gender") String gender,
            @Param("vector") PGvector vector
    );
}
