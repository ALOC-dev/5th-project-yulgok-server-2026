package com.dormmatch.domain.chat.service;

import com.dormmatch.domain.chat.dto.ChatMessageResponseDto;
import com.dormmatch.domain.chat.dto.ChatMessagesResponseDto;
import com.dormmatch.domain.chat.dto.ChatReadResponseDto;
import com.dormmatch.domain.chat.dto.ChatRoomResponseDto;
import com.dormmatch.domain.chat.dto.ChatRoomsResponseDto;
import com.dormmatch.domain.chat.dto.ChatUnreadCountResponseDto;
import com.dormmatch.domain.chat.entity.ChatMessage;
import com.dormmatch.domain.chat.entity.ChatRoom;
import com.dormmatch.domain.chat.repository.ChatMessageRepository;
import com.dormmatch.domain.chat.repository.ChatRoomRepository;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import com.dormmatch.global.util.HashIdsUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public ChatRoomsResponseDto getChatRooms(String userHashId) {
        Long userId = HashIdsUtils.decode(userHashId);

        // TODO: MatchRequest 도메인 연결 후 현재 유저가 참여한 채팅방만 조회하도록 수정
        List<ChatRoomResponseDto> rooms = chatRoomRepository.findAll()
                .stream()
                .map(room -> {
                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);

                    String lastMessageText = lastMessage == null ? null : lastMessage.getMessage();
                    LocalDateTime lastMessageTime = lastMessage == null ? null : lastMessage.getCreatedAt();

                    int unreadCount = chatMessageRepository
                            .countByRoomIdAndSenderIdNotAndIsReadFalse(room.getId(), userId);

                    // TODO: Users 도메인 연결 후 실제 상대방 이름/프로필 이미지로 교체
                    return new ChatRoomResponseDto(
                            room.getId(),
                            "상대방",
                            null,
                            lastMessageText,
                            lastMessageTime,
                            unreadCount
                    );
                })
                .toList();

        return new ChatRoomsResponseDto(rooms);
    }

    @Transactional(readOnly = true)
    public ChatMessagesResponseDto getMessages(Long roomId, Long cursor, int size) {
        validateRoomExists(roomId);

        // size + 1개를 조회해서 다음 페이지 존재 여부를 판단한다.
        PageRequest pageRequest = PageRequest.of(0, size + 1);

        List<ChatMessage> messages = cursor == null
                ? chatMessageRepository.findByRoomIdOrderByIdDesc(roomId, pageRequest)
                : chatMessageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, cursor, pageRequest);

        boolean hasNext = messages.size() > size;

        List<ChatMessageResponseDto> responseMessages = messages.stream()
                .limit(size)
                .map(ChatMessageResponseDto::from)
                .toList();

        return new ChatMessagesResponseDto(responseMessages, hasNext);
    }

    @Transactional
    public ChatReadResponseDto markMessagesAsRead(Long roomId, String userHashId) {
        Long userId = HashIdsUtils.decode(userHashId);

        validateRoomExists(roomId);

        // 내가 보낸 메시지는 읽음 처리 대상에서 제외한다.
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByRoomIdAndSenderIdNotAndIsReadFalse(roomId, userId);

        unreadMessages.forEach(ChatMessage::markAsRead);

        return new ChatReadResponseDto(true);
    }

    @Transactional(readOnly = true)
    public ChatUnreadCountResponseDto getTotalUnreadCount(String userHashId) {
        Long userId = HashIdsUtils.decode(userHashId);

        // TODO: 현재 유저가 참여한 채팅방의 안 읽은 메시지만 집계하도록 수정
        int totalUnreadCount = chatMessageRepository.countBySenderIdNotAndIsReadFalse(userId);

        return new ChatUnreadCountResponseDto(totalUnreadCount);
    }

    @Transactional
    public ChatMessageResponseDto sendMessage(Long roomId, String senderHashId, String message) {
        Long senderId = HashIdsUtils.decode(senderHashId);

        ChatRoom chatRoom = getChatRoom(roomId);
        validateSendable(chatRoom, senderId, message);

        ChatMessage chatMessage = ChatMessage.create(roomId, senderId, message.trim());
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(savedMessage);
    }

    private ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    private void validateRoomExists(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    private void validateSendable(ChatRoom chatRoom, Long senderId, String message) {
        if (chatRoom.isClosed()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_CLOSED);
        }

        // TODO: MatchRequest 도메인 연결 후 senderId가 해당 채팅방 참여자인지 검증
        if (senderId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CHAT_MESSAGE);
        }

        if (message.length() > 500) {
            throw new BusinessException(ErrorCode.CHAT_MESSAGE_TOO_LONG);
        }
    }
}
