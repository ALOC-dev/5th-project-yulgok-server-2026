package com.dormmatch.domain.matching.controller;

import com.dormmatch.domain.matching.dto.MatchingRequestDto;
import com.dormmatch.domain.matching.dto.MatchingResponseDto;
import com.dormmatch.domain.matching.entity.MatchRequests;
import com.dormmatch.domain.matching.repository.MatchRepository;
import com.dormmatch.domain.matching.service.MatchingService;
import com.dormmatch.global.aop.RequiresAuth;
import com.dormmatch.global.aop.RequiresCertification;
import com.dormmatch.global.aop.RequiresSurvey;
import com.dormmatch.global.response.GlobalApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/matching")
public class MatchingController {


    private final MatchingService matchingService;

    @Autowired
    public MatchingController(MatchingService matchingService){
        this.matchingService = matchingService;
    }



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching status retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Certification not completed"),
            @ApiResponse(responseCode = "404", description = "Matching status not found")
    })
    @RequiresAuth   @RequiresCertification  @RequiresSurvey
    @GetMapping("/api/matching/status/")
    public ResponseEntity<GlobalApiResponse<?>> getMatchingStatus(
            @AuthenticationPrincipal Long userId
    ){
        List<MatchingResponseDto> responseDtos = matchingService.getMatchingStatus(userId);

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,"매칭 카드 조회 성공",responseDtos));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Matching request updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Survey not completed"),
            @ApiResponse(responseCode = "409", description = "Matching request already exists")
    })
    @RequiresAuth   @RequiresCertification  @RequiresSurvey
    @PatchMapping("/api/matching/requests")
    public ResponseEntity<GlobalApiResponse<?>> sendHeartToReceiver(
            @AuthenticationPrincipal Long userId,
            @RequestBody MatchingRequestDto requestDto
    ){

        if(requestDto.getMatchStatus().equals("REJECT")){
            matchingService.rejectHeart(userId, requestDto.getReceiverId());
            return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,"하트 거절 성공",null));
        }

        String success = matchingService.sendHeartToReceiver(userId, requestDto.getReceiverId());

        if(success.equals("SENT"))
            return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,"하트 전송 성공",null));
        else
            return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,"매칭 수락 성공",null));
    }

}
