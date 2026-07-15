package com.irummate.domain.chat.dto;

import com.irummate.domain.chat.entity.ChatRoomStatus;

public record ChatRoomPartnerDto(
        Long roomId,
        String partnerName,
        String partnerProfileImageUrl,
        ChatRoomStatus status
) {
}
