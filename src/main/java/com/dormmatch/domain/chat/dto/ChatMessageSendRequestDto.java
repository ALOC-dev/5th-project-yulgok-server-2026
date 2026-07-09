package com.dormmatch.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageSendRequestDto {

    // TODO: WebSocket JWT 인증 연결 후 senderId는 토큰에서 가져오도록 변경
    private Long senderId;

    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
    @Size(max = 500, message = "메시지는 500자를 초과할 수 없습니다.")
    private String message;
}