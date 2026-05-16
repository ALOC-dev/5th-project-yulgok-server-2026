package com.dormmatch.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String content;
}