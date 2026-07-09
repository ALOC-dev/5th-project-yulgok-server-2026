package com.dormmatch.domain.chat.controller;

import com.dormmatch.domain.chat.dto.ChatMessagesResponseDto;
import com.dormmatch.domain.chat.dto.ChatReadResponseDto;
import com.dormmatch.domain.chat.dto.ChatRoomsResponseDto;
import com.dormmatch.domain.chat.dto.ChatUnreadCountResponseDto;
import com.dormmatch.domain.chat.service.ChatService;
import com.dormmatch.global.response.GlobalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<GlobalApiResponse<ChatRoomsResponseDto>> getChatRooms(
            @RequestParam(defaultValue = "4m0ZGlD3") String userId
    ) {
        ChatRoomsResponseDto responseDto = chatService.getChatRooms(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "Chat rooms fetched.", responseDto)
        );
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<GlobalApiResponse<ChatMessagesResponseDto>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    ) {
        ChatMessagesResponseDto responseDto = chatService.getMessages(roomId, cursor, size);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "Chat messages fetched.", responseDto)
        );
    }

    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<GlobalApiResponse<ChatReadResponseDto>> markMessagesAsRead(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "4m0ZGlD3") String userId
    ) {
        ChatReadResponseDto responseDto = chatService.markMessagesAsRead(roomId, userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "Messages marked as read.", responseDto)
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<GlobalApiResponse<ChatUnreadCountResponseDto>> getTotalUnreadCount(
            @RequestParam(defaultValue = "4m0ZGlD3") String userId
    ) {
        ChatUnreadCountResponseDto responseDto = chatService.getTotalUnreadCount(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "Unread count fetched.", responseDto)
        );
    }
}
