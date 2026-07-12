package com.irummate.domain.chat.dto;

public record ChatRoomPartnerDto(
        Long roomId,
        String partnerName,
        String partnerProfileImageUrl
) {
}