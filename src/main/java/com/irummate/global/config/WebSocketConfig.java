package com.irummate.global.config;

import com.irummate.global.jwt.JwtTokenProvider;
import com.irummate.global.jwt.WebSocketPrincipal;
import com.irummate.global.util.HashIdsUtils;
import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat") // 프론트에서 최초로 웹소켓을 연결할 endpoint
                .setAllowedOriginPatterns("*") // 로컬 테스트를 위해 모든 origin 접속을 허용
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // [SUB] 프론트에서 메시지를 수신하기 위해 구독하는 주소 prefix
        // /topic은 채팅방 단위 브로드캐스트, /queue는 개인 알림용으로 사용한다.
        registry.enableSimpleBroker("/topic", "/queue");

        // [PUB] 프론트에서 백엔드로 메시지를 보낼 때 사용하는 주소 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = resolveToken(accessor);

                    if (token == null || !jwtTokenProvider.validateToken(token)) {
                        throw new IllegalArgumentException("웹소켓 인증에 실패했습니다.");
                    }

                    Claims claims = jwtTokenProvider.parseClaims(token);
                    Long userId = HashIdsUtils.decode(claims.getSubject());
                    accessor.setUser(new WebSocketPrincipal(userId));
                }

                return message;
            }
        });
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization == null) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
