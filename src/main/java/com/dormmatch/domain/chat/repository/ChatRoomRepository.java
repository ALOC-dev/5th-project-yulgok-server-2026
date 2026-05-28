package com.dormmatch.domain.chat.repository;

import com.dormmatch.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMatchRequestId(Long matchRequestId);
}