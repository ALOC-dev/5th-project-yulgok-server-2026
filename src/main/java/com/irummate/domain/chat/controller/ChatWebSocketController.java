package com.irummate.domain.chat.controller;

import com.irummate.domain.chat.dto.ChatMessageResponseDto;
import com.irummate.domain.chat.dto.ChatMessageSendRequestDto;
import com.irummate.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            ChatService chatService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/rooms/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Valid @Payload ChatMessageSendRequestDto requestDto
    ) {
        ChatMessageResponseDto responseDto = chatService.sendMessage(
                roomId,
                requestDto.getSenderId(),
                requestDto.getMessage()
        );

        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, responseDto);
    }
}
