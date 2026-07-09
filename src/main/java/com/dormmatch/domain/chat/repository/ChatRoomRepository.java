package com.dormmatch.domain.chat.repository;

import com.dormmatch.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 매칭 요청 수락 시 같은 matchRequestId로 채팅방이 중복 생성되는 것을 막기 위해 사용한다.
    Optional<ChatRoom> findByMatchRequestId(Long matchRequestId);
}