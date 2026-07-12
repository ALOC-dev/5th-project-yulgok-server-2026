package com.irummate.domain.matching.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.irummate.domain.matching.entity.MatchRequests;

@Repository
public interface MatchRepository extends JpaRepository<MatchRequests, Long> {

    @Query("""
    SELECT mr
    FROM MatchRequests mr
    WHERE (
        mr.userLow.id = :userId
        AND mr.userLowStatus = com.irummate.domain.matching.entity.MatchStatus.NONE
        AND mr.userHighStatus = com.irummate.domain.matching.entity.MatchStatus.RECOMMENDED
        AND mr.userHighPreferences.smokingStatus = :smokingStatus
    )
    OR (
        mr.userHigh.id = :userId
        AND mr.userHighStatus = com.irummate.domain.matching.entity.MatchStatus.NONE
        AND mr.userLowStatus = com.irummate.domain.matching.entity.MatchStatus.RECOMMENDED
        AND mr.userLowPreferences.smokingStatus = :smokingStatus
    )
    ORDER BY mr.matchPercentage DESC
""")
    List<MatchRequests> findReusableCandidatesWithSmoking(
            @Param("userId") Long userId,
            @Param("smokingStatus") Integer smokingStatus,
            Pageable pageable
    );



    @Query("""
    SELECT mr
    FROM MatchRequests mr
    WHERE (
        mr.userLow.id = :userId
        AND mr.userLowStatus = com.irummate.domain.matching.entity.MatchStatus.NONE
        AND mr.userHighStatus = com.irummate.domain.matching.entity.MatchStatus.RECOMMENDED
        
    )
    OR (
        mr.userHigh.id = :userId
        AND mr.userHighStatus = com.irummate.domain.matching.entity.MatchStatus.NONE
        AND mr.userLowStatus = com.irummate.domain.matching.entity.MatchStatus.RECOMMENDED
    )
    ORDER BY mr.matchPercentage DESC
""")
    List<MatchRequests> findReusableCandidatesIgnoringSmoking(
            @Param("userId") Long userId,
            Pageable pageable
    );


    // ?먭린 ?먯떊???ы븿??match瑜?議고쉶
// (?곷?諛⑹씠 嫄곗젅??寃껋? ?ы븿)
@Query("""
SELECT DISTINCT mr
FROM MatchRequests mr
JOIN FETCH mr.userLow ul
JOIN FETCH ul.userDetails
JOIN FETCH ul.userPreferences
JOIN FETCH mr.userHigh uh
JOIN FETCH uh.userDetails
JOIN FETCH uh.userPreferences
WHERE (
    mr.userLow.id = :userId
    AND mr.userLowStatus NOT IN (
        com.irummate.domain.matching.entity.MatchStatus.NONE,
        com.irummate.domain.matching.entity.MatchStatus.REJECTED,
        com.irummate.domain.matching.entity.MatchStatus.CLOSED
    )
)
OR (
    mr.userHigh.id = :userId
    AND mr.userHighStatus NOT IN (
        com.irummate.domain.matching.entity.MatchStatus.NONE,
        com.irummate.domain.matching.entity.MatchStatus.REJECTED,
        com.irummate.domain.matching.entity.MatchStatus.CLOSED
    )
)
ORDER BY mr.matchPercentage DESC
""")
List<MatchRequests> findAllVisibleByUserId(@Param("userId") Long userId);


    @Query("""
    SELECT mr
    FROM MatchRequests mr
    WHERE mr.id <> :confirmedMatchRequestId
    AND (
        mr.userLow.id = :userId
        OR mr.userHigh.id = :userId
    )
    """)
    List<MatchRequests> findAllByUserIdExceptConfirmed(
            @Param("userId") Long userId,
            @Param("confirmedMatchRequestId") Long confirmedMatchRequestId
    );


    @Query("""
    SELECT mr
    FROM MatchRequests mr
    WHERE mr.userLow.id = LEAST(:userId1, :userId2)
    AND mr.userHigh.id = GREATEST(:userId1, :userId2)
    """)
    Optional<MatchRequests> findByIds(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );
}
