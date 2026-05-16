package com.dormmatch.domain.chat.controller;

import com.dormmatch.domain.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.send") // 프론트에서 "/app/chat.send" 로 메시지 보내면 메소드 실행 됨.
    @SendTo("/topic/room/test") // 메소드 처리 후 "/topic/room/test"를 Subscribe 중인 user에게 결과 쏴줌.
    public ChatMessageDto echoMessage(ChatMessageDto message)
    {
        System.out.println("메아리 서버 수신 완료");
        System.out.println("보낸 사람 : " + message.getSender());
        System.out.println("메시지 내용 : " + message.getContent());

        return message;
    }
}
