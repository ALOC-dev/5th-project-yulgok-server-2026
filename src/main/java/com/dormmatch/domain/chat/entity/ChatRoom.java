package com.dormmatch.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //MatchRequest 엔티티 생기면 수정하자.
    @Column(name = "match_request_id", nullable = false, unique = true)
    private Long matchRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status = ChatRoomStatus.OPEN;

    @Column(name = "sender_selected", nullable = false)
    private Boolean senderSelected = false;

    @Column(name = "receiver_selected", nullable = false)
    private Boolean receiverSelected = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void selectBySender() {
        this.senderSelected = true;
    }

    public void selectByReceiver() {
        this.receiverSelected = true;
    }

    public boolean isBothSelected() {
        return Boolean.TRUE.equals(senderSelected)
                && Boolean.TRUE.equals(receiverSelected);
    }

    @PrePersist
    protected void onCreate()
    {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isClosed()
    {
        return this.status == ChatRoomStatus.CLOSED;
    }
    public void close()
    {
        this.status = ChatRoomStatus.CLOSED;
    }
}
