package com.dormmatch.domain.chat.dto;

public record ChatRoomUnreadCountDto(
        Long roomId,
        Long unreadCount
) {
}