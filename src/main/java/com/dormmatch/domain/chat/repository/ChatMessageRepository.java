package com.dormmatch.domain.chat.repository;

import com.dormmatch.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//서비스에 필요한 메소드들 정의
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방 메시지 최신순으로 가져오는 메소드
    List<ChatMessage> findByRoomIdOrderByIdDesc(Long roomId, Pageable pageable);

    // 커서 기반 페이지네이션용 (ex. 처음 메시지 30개 가져왔고, 마지막 메시지id가 150이면 id<150인 메시지 30개 가져옴)
    List<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long cursor, Pageable pageable);

    //채팅방 목록 내 마지막 메시지 보여주기 위함.
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(Long roomId);
    //Optional인 이유는 채팅방만 있고 메시지는 없을 수 있으니.

    // 특정 채팅방에서 내가 안 읽은 메시지 개수 조회 
    int countByRoomIdAndSenderIdNotAndIsReadFalse(Long roomId, Long senderId);

    // 전체 채팅방에서 내가 안 읽은 총 메시지 개수 조회(테스트 용, 추후 연결할 때 수정 필요함)
    int countBySenderIdNotAndIsReadFalse(Long senderId);

    // 읽음 처리용 메소드
    List<ChatMessage> findByRoomIdAndSenderIdNotAndIsReadFalse(Long roomId, Long senderId);
}