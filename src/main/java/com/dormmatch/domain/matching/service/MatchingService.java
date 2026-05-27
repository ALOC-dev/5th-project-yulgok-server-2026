package com.dormmatch.domain.matching.service;

import com.dormmatch.domain.matching.dto.MatchingResponseDto;
import com.dormmatch.domain.matching.entity.MatchRequests;
import com.dormmatch.domain.matching.entity.MatchStatus;
import com.dormmatch.domain.matching.repository.MatchRepository;
import com.dormmatch.domain.matching.util.MatchingDtoMapper;
import com.dormmatch.domain.user.entity.UserDetails;
import com.dormmatch.domain.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingService {

    private MatchRepository matchRepository;

    @Autowired
    public MatchingService(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }

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


        // 하트를 보내고 대기중인 상대방들
        List<MatchRequests> sentMatchRequests = matchRepository.findBySenderIdAndStatusIn(userId, List.of(MatchStatus.SENT));

        if(!sentMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : sentMatchRequests) {
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


        // 양방향 하트 수락
        List<MatchRequests> partialConfirmedMatchRequests = matchRepository.findByConditions(userId, userId, List.of(MatchStatus.PARTIAL_CONFIRMED));

        if(!partialConfirmedMatchRequests.isEmpty()) {
            for (MatchRequests matchRequests : partialConfirmedMatchRequests) {
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

        return matchingResponseDtos;
    }
}
