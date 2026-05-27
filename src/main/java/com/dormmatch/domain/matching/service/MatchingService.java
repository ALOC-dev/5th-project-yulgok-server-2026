package com.dormmatch.domain.matching.service;

import com.dormmatch.domain.matching.dto.MatchingResponseDto;
import com.dormmatch.domain.matching.entity.MatchRequests;
import com.dormmatch.domain.matching.entity.MatchStatus;
import com.dormmatch.domain.matching.repository.MatchRepository;
import com.dormmatch.domain.matching.util.MatchingDtoMapper;
import com.dormmatch.domain.survey.entity.UserPreferences;
import com.dormmatch.domain.user.entity.UserDetails;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MatchingService {

    private MatchRepository matchRepository;

    @Autowired
    public MatchingService(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchingResponseDto> getMatchingStatus(Long userId){


        // 매칭이 완료된 상태면 완료된 상대방만 보냄
        MatchRequests completedMatchRequests = matchRepository.findByUserIdAndStatus(userId, MatchStatus.CONFIRMED)
                .isPresent() ? matchRepository.findByUserIdAndStatus(userId, MatchStatus.CONFIRMED).get() : null;

        if(completedMatchRequests != null)
            return List.of(MatchingDtoMapper.toResponseDto(completedMatchRequests));

        List<MatchingResponseDto> matchingResponseDtos = new ArrayList<MatchingResponseDto>();

        // 하트를 보낸 상대방들
        List<MatchRequests> receivedMatchRequests = matchRepository.findByReceiverIdAndStatusIn(userId, List.of(MatchStatus.SENT));

        if(!receivedMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : receivedMatchRequests) {
                Users user = matchRequests.getSender();
                UserDetails userDetails = user.getUserDetails();
                MatchingResponseDto matchingResponseDto = MatchingResponseDto.builder()
                        .userId(user.getId().toString())
                        .name(user.getNickname())
                        .gender(userDetails.getGender())
                        .age(userDetails.getAge())
                        .introduce(matchRequests.getSenderPreferences().getIntroduce())
                        .department(userDetails.getDepartment())
                        .studentId(userDetails.getStudentId())
                        .matchPercentage(matchRequests.getMatchPercentage())
                        .matchStatus(matchRequests.getStatus())
                        .build();
                matchingResponseDtos.add(matchingResponseDto);
            }
        }


        // 추천받은 상대방들
        List<MatchRequests> recommendedMatchRequests = matchRepository.findBySenderIdAndStatusIn(userId, List.of(MatchStatus.RECOMMENDED));

        if(!recommendedMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : recommendedMatchRequests) {
                Users user = matchRequests.getReceiver();
                UserDetails userDetails = user.getUserDetails();
                MatchingResponseDto matchingResponseDto = MatchingResponseDto.builder()
                        .userId(user.getId().toString())
                        .name(user.getNickname())
                        .gender(userDetails.getGender())
                        .age(userDetails.getAge())
                        .introduce(matchRequests.getReceiverPreferences().getIntroduce())
                        .department(userDetails.getDepartment())
                        .studentId(userDetails.getStudentId())
                        .matchPercentage(matchRequests.getMatchPercentage())
                        .matchStatus(matchRequests.getStatus())
                        .build();
                matchingResponseDtos.add(matchingResponseDto);
            }
        }


        // 하트를 보내고 응답을 대기중인 상대방들
        List<MatchRequests> sentMatchRequests = matchRepository.findBySenderIdAndStatusIn(userId, List.of(MatchStatus.SENT));

        if(!sentMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : sentMatchRequests) {
                Users user = matchRequests.getReceiver();
                UserDetails userDetails = user.getUserDetails();
                MatchingResponseDto matchingResponseDto = MatchingResponseDto.builder()
                        .userId(user.getId().toString())
                        .name(user.getNickname())
                        .gender(userDetails.getGender())
                        .age(userDetails.getAge())
                        .introduce(matchRequests.getReceiverPreferences().getIntroduce())
                        .department(userDetails.getDepartment())
                        .studentId(userDetails.getStudentId())
                        .matchPercentage(matchRequests.getMatchPercentage())
                        .matchStatus(matchRequests.getStatus())
                        .build();
                matchingResponseDtos.add(matchingResponseDto);
            }
        }


        // 양방향 하트 수락
        List<MatchRequests> partialConfirmedMatchRequests = matchRepository.findByConditions(userId, userId, List.of(MatchStatus.PARTIAL_CONFIRMED));

        if(!partialConfirmedMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : partialConfirmedMatchRequests) {
                Users user = matchRequests.getReceiver().getId().equals(userId) ? matchRequests.getSender() : matchRequests.getReceiver();
                UserDetails userDetails = user.getUserDetails();
                MatchingResponseDto matchingResponseDto = MatchingResponseDto.builder()
                        .userId(user.getId().toString())
                        .name(user.getNickname())
                        .gender(userDetails.getGender())
                        .age(userDetails.getAge())
                        .introduce(matchRequests.getReceiverPreferences().getIntroduce())
                        .department(userDetails.getDepartment())
                        .studentId(userDetails.getStudentId())
                        .matchPercentage(matchRequests.getMatchPercentage())
                        .matchStatus(matchRequests.getStatus())
                        .build();
                matchingResponseDtos.add(matchingResponseDto);
            }
        }

        if(matchingResponseDtos.isEmpty()) {
            throw new BusinessException(ErrorCode.MATCH_REQUEST_NOT_FOUND);
        }

        return matchingResponseDtos;
    }


    @Transactional
    public String sendHeartToReceiver(Long userId, Long receiverId){

        List<MatchRequests> matchRequests = matchRepository.findByConditions(userId, receiverId, List.of(MatchStatus.RECOMMENDED));

        if(!matchRequests.isEmpty()) {
            log.info("Heart sent successfully for user {} and receiver {}", userId, receiverId);
            matchRequests.forEach(matchRequests1 -> matchRequests1.updateStatus(MatchStatus.SENT));
            return "SENT";
        }

        List<MatchRequests> matchRequests2 = matchRepository.findByConditions(receiverId, userId, List.of(MatchStatus.SENT));

        if(matchRequests2.isEmpty()) {
            log.warn("No matching request found for user {} and receiver {}", userId, receiverId);
            throw new BusinessException(ErrorCode.MATCH_REQUEST_NOT_FOUND);
        }

        matchRequests2.forEach(matchRequests1 -> matchRequests1.updateStatus(MatchStatus.PARTIAL_CONFIRMED));

        UserPreferences userPreferences = matchRequests2.get(0).getSenderPreferences();
        userPreferences.updateIsMatched();
        userPreferences = matchRequests2.get(0).getReceiverPreferences();
        userPreferences.updateIsMatched();


        log.info("Match made between user {} and receiver {}", userId, receiverId);
        return "PARTIAL_CONFIRMED";
    }

    @Transactional
    public void rejectHeart(Long userId, Long receiverId){
        List<MatchRequests> matchRequests = matchRepository.findByConditions(userId, receiverId, List.of(MatchStatus.SENT));

        if(!matchRequests.isEmpty()) {
            matchRequests.forEach(matchRequests1 -> matchRequests1.updateStatus(MatchStatus.REJECTED));
            log.info("Heart rejection successful for user {} and receiver {}", userId, receiverId);
            return;
        }

        List<MatchRequests> matchRequests2 = matchRepository.findByConditions(receiverId, userId, List.of(MatchStatus.RECOMMENDED));

        if(!matchRequests2.isEmpty()) {
            log.info("Heart rejection successful for user {} and receiver {}", userId, receiverId);
            matchRequests2.forEach(matchRequests1 -> matchRequests1.updateStatus(MatchStatus.REJECTED));
        }

        log.warn("No matching request found for user {} and receiver {}", userId, receiverId);
        throw new BusinessException(ErrorCode.MATCH_REQUEST_NOT_FOUND);
    }
}
