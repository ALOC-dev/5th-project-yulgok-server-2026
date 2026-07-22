package com.irummate.domain.chat.dto;

import com.irummate.domain.chat.entity.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponseDto { //JSON 응답 Key 설정 

    private Long roomId;
    private String partnerId;
    private String partnerName;
    private String partnerProfileImageUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private ChatRoomStatus status;
}
