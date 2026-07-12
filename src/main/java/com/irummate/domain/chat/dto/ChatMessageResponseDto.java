package com.irummate.domain.chat.dto;

import com.irummate.domain.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
// 채팅방 내 과거 메시지 조회용(메시지 하나 조회)
@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long messageId;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return new ChatMessageResponseDto(
                chatMessage.getId(),
                chatMessage.getSenderId(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt(),
                chatMessage.getIsRead()
        );
    }
}