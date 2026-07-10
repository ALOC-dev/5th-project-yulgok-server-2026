package com.irummate.domain.chat.repository;

import com.irummate.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 매칭 요청 하나당 채팅방이 하나만 생성되도록 확인할 때 사용한다.
    Optional<ChatRoom> findByMatchRequestId(Long matchRequestId);

    // chat_rooms.match_request_id를 match_requests와 연결해서 현재 유저가 참여한 채팅방만 조회한다.
    @Query("""
            SELECT cr
            FROM ChatRoom cr
            JOIN MatchRequests mr ON mr.id = cr.matchRequestId
            WHERE mr.userLow.id = :userId
               OR mr.userHigh.id = :userId
            ORDER BY cr.createdAt DESC
            """)
    List<ChatRoom> findAllByParticipantId(@Param("userId") Long userId);
}
