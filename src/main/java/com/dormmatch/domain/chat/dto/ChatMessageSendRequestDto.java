package com.dormmatch.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageSendRequestDto {

    // TODO: WebSocket JWT authentication should provide senderId from token.
    private String senderId;

    @NotBlank(message = "message must not be blank.")
    @Size(max = 500, message = "message must be 500 characters or less.")
    private String message;
}
