package com.dormmatch.domain.chat.controller;

import com.dormmatch.domain.chat.dto.ChatMessagesResponseDto;
import com.dormmatch.domain.chat.dto.ChatReadResponseDto;
import com.dormmatch.domain.chat.dto.ChatRoomsResponseDto;
import com.dormmatch.domain.chat.dto.ChatUnreadCountResponseDto;
import com.dormmatch.domain.chat.service.ChatService;
import com.dormmatch.global.aop.RequiresAuth;
import com.dormmatch.global.response.GlobalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    @RequiresAuth
    public ResponseEntity<GlobalApiResponse<ChatRoomsResponseDto>> getChatRooms(
            @AuthenticationPrincipal Long userId
    ) {
        ChatRoomsResponseDto responseDto = chatService.getChatRooms(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "채팅방 목록 조회 성공", responseDto)
        );
    }

    // 채팅방 내 과거 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    @RequiresAuth
    public ResponseEntity<GlobalApiResponse<ChatMessagesResponseDto>> getMessages(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    ) {
        // TODO: 매칭/유저 도메인 연결 후 roomId에 대한 참여자 검증 추가
        ChatMessagesResponseDto responseDto = chatService.getMessages(roomId, cursor, size);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "채팅방 메시지 조회 성공", responseDto)
        );
    }

    // 메시지 읽음 처리
    @PatchMapping("/rooms/{roomId}/read")
    @RequiresAuth
    public ResponseEntity<GlobalApiResponse<ChatReadResponseDto>> markMessagesAsRead(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId
    ) {
        ChatReadResponseDto responseDto = chatService.markMessagesAsRead(roomId, userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "메시지 읽음 처리 성공", responseDto)
        );
    }

    // 안 읽은 메시지 전체 개수 조회
    @GetMapping("/unread-count")
    @RequiresAuth
    public ResponseEntity<GlobalApiResponse<ChatUnreadCountResponseDto>> getTotalUnreadCount(
            @AuthenticationPrincipal Long userId
    ) {
        ChatUnreadCountResponseDto responseDto = chatService.getTotalUnreadCount(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "안 읽은 메시지 개수 조회 성공", responseDto)
        );
    }
}