package com.dormmatch.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry.addEndpoint("/ws-stomp") // 프론트에서 최초로 웹소켓을 연결할 주소, ws://[서버IP]:8080/ws/chat
                .setAllowedOriginPatterns("*") // '*'은 로컬에서 테스트하기 위해 모든 도메인 접속 허용
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        // [SUB] 프론트에서 메시지를 수신 받기 위해 Subscribe할 주소 접두사.
        // /topic 은 1:N (채팅방 내), queue 는 1:1 (개인 알림)에 사용
        registry.enableSimpleBroker("/topic", "/queue");

        // [PUB] 프론트에서 백엔드로 메시지를 보낼 때 Publish할 주소 접두사.
        registry.setApplicationDestinationPrefixes("/app");
    }
}
