package com.dormmatch.domain.matching.entity;

public enum MatchStatus {
    RECOMMENDED,        // 추천된 상태
    SENT,               // 상대방에게 하트 전송
    PARTIAL_CONFIRMED,  // 하트 수락(채팅방 개설)
    CONFIRMED,          // 채팅 이후 최종 결정 성공
    REJECTED            // 거절(하트 거절, 최종 결정 거절)
}
