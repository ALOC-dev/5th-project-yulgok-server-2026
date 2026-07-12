package com.dormmatch.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomLastMessageDto(
        Long roomId,
        String lastMessage,
        LocalDateTime lastMessageTime
) {
}
