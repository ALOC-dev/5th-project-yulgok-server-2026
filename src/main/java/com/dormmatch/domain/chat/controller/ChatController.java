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
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<GlobalApiResponse<ChatRoomsResponseDto>> getChatRooms(
            // TODO: 인증 연동 후 @AuthenticationPrincipal Long userId로 변경
            @RequestParam(defaultValue = "1") Long userId
    ) {
        ChatRoomsResponseDto responseDto = chatService.getChatRooms(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "채팅방 목록 조회 성공", responseDto)
        );
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<GlobalApiResponse<ChatMessagesResponseDto>> getMessages(
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

    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<GlobalApiResponse<ChatReadResponseDto>> markMessagesAsRead(
            @PathVariable Long roomId,
            // TODO: 인증 연동 후 @AuthenticationPrincipal Long userId로 변경
            @RequestParam(defaultValue = "1") Long userId
    ) {
        ChatReadResponseDto responseDto = chatService.markMessagesAsRead(roomId, userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "메시지 읽음 처리 성공", responseDto)
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<GlobalApiResponse<ChatUnreadCountResponseDto>> getTotalUnreadCount(
            // TODO: 인증 연동 후 @AuthenticationPrincipal Long userId로 변경
            @RequestParam(defaultValue = "1") Long userId
    ) {
        ChatUnreadCountResponseDto responseDto = chatService.getTotalUnreadCount(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "안 읽은 메시지 개수 조회 성공", responseDto)
        );
    }
}
