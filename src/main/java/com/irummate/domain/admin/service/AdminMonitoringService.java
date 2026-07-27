package com.irummate.domain.admin.service;

import com.irummate.domain.admin.dto.AdminMonitoringSummaryResponseDto;
import com.irummate.domain.chat.entity.ChatRoomStatus;
import com.irummate.domain.chat.repository.ChatMessageRepository;
import com.irummate.domain.chat.repository.ChatRoomRepository;
import com.irummate.domain.matching.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMonitoringService {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final MatchRepository matchRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AdminMonitoringSummaryResponseDto getSummary() {
        LocalDateTime todayStart = LocalDate.now(SERVICE_ZONE).atStartOfDay();

        return new AdminMonitoringSummaryResponseDto(
                new AdminMonitoringSummaryResponseDto.MatchingSummary(
                        matchRepository.countHeartSent(),
                        matchRepository.countHeartMatched(),
                        matchRepository.countConfirmPending(),
                        matchRepository.countFinalConfirmed(),
                        matchRepository.countClosed()
                ),
                new AdminMonitoringSummaryResponseDto.ChatSummary(
                        chatRoomRepository.count(),
                        chatRoomRepository.countByStatus(ChatRoomStatus.OPEN),
                        chatRoomRepository.countByStatus(ChatRoomStatus.CLOSED),
                        chatMessageRepository.count(),
                        chatMessageRepository.countByCreatedAtGreaterThanEqual(todayStart)
                ),
                LocalDateTime.now(SERVICE_ZONE)
        );
    }
}
