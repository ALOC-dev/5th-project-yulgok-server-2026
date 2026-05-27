package com.dormmatch.domain.matching.repository;

import com.dormmatch.domain.matching.entity.MatchRequests;
import com.dormmatch.domain.matching.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchRequests, Long> {
    @Query("""
    SELECT m FROM MatchRequests m 
    WHERE (m.sender.id = :senderId OR m.receiver.id = :receiverId) 
    AND m.status = :statuses
    """)
    List<MatchRequests> findByConditions(@Param("senderId") Long senderId,
                                         @Param("receiverId") Long receiverId,
                                         @Param("statuses")List<MatchStatus> statuses);

    List<MatchRequests> findBySenderIdAndStatusIn(Long senderId, List<MatchStatus> status);

    List<MatchRequests> findByReceiverIdAndStatusIn(Long receiverId, List<MatchStatus> status);

    @Query("""
    SELECT m FROM MatchRequests m
    WHERE (m.sender.id = :userId OR m.receiver.id = :userId)
      AND m.status = :status
    """)
    Optional<MatchRequests> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") MatchStatus status
    );
}
