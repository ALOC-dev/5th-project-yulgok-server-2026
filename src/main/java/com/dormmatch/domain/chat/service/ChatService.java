package com.dormmatch.domain.chat.service;

//ChatService의 주요 기능
//1. 채팅방 목록 조회
//2. 채팅방 과거 메시지 조회
//3. 메시지 읽음 처리
//4. 전체 안 읽은 메시지 개수 조회

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

    //채팅방 목록 조회
    @Transactional(readOnly = true)
    public ChatRoomsResponseDto getChatRooms(Long userId) {
        // 매칭/유저 도메인 연결 전까지는 전체 채팅방을 조회한다. (나중에 유저가 참여한 채팅방만 조회하는 걸로 수정해야함)
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

                    // 유저 도메인 연결 후 실제 상대방 이름/프로필 이미지로 교체할 예정, 지금은 임시값 넣어둠.
                    // TODO: Users 도메인 연결 후 실제 상대방 정보로 교체
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

    //채팅방 내 과거 메시지 조회
    @Transactional(readOnly = true)
    public ChatMessagesResponseDto getMessages(Long roomId, Long cursor, int size) {
        //채팅방이 실제 존재하는 지 확인, 아니면 CHAT_ROOM_NOT_FOUND 예외 던짐.
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
    public ChatReadResponseDto markMessagesAsRead(Long roomId, Long userId) {
        validateRoomExists(roomId);

        // 내가 보낸 메시지는 읽음 처리 대상에서 제외한다.
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByRoomIdAndSenderIdNotAndIsReadFalse(roomId, userId);

        unreadMessages.forEach(ChatMessage::markAsRead);

        return new ChatReadResponseDto(true);
    }

    // 나중에 내가 참여한 방 기준으로 수정해야함.
    @Transactional(readOnly = true)
    public ChatUnreadCountResponseDto getTotalUnreadCount(Long userId) {
        int totalUnreadCount = chatMessageRepository.countBySenderIdNotAndIsReadFalse(userId);

        return new ChatUnreadCountResponseDto(totalUnreadCount);
    }

    private void validateRoomExists(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }
}