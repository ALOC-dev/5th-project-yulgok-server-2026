package com.dormmatch.domain.chat.repository;

import com.dormmatch.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지를 최신순으로 조회한다.
    List<ChatMessage> findByRoomIdOrderByIdDesc(Long roomId, Pageable pageable);

    // cursor보다 오래된 메시지를 최신순으로 조회한다.
    List<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long cursor, Pageable pageable);

    // 채팅방 목록에서 마지막 메시지를 보여주기 위해 사용한다.
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(Long roomId);

    // 특정 채팅방에서 내가 읽지 않은 메시지 수를 조회한다.
    int countByRoomIdAndSenderIdNotAndIsReadFalse(Long roomId, Long senderId);

    // 현재 유저가 참여한 채팅방 안에서만 안 읽은 메시지 총합을 조회한다.
    @Query("""
            SELECT COUNT(cm)
            FROM ChatMessage cm
            JOIN ChatRoom cr ON cr.id = cm.roomId
            JOIN MatchRequests mr ON mr.id = cr.matchRequestId
            WHERE (mr.userLow.id = :userId OR mr.userHigh.id = :userId)
              AND cm.senderId <> :userId
              AND cm.isRead = false
            """)
    int countUnreadByParticipantId(@Param("userId") Long userId);

    // 읽음 처리 대상 메시지를 조회한다.
    List<ChatMessage> findByRoomIdAndSenderIdNotAndIsReadFalse(Long roomId, Long senderId);
}
