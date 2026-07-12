package com.irummate.domain.matching.controller;

import com.irummate.domain.matching.dto.MatchingConfigDto;
import com.irummate.domain.matching.service.MatchingConfigService;
import com.irummate.global.aop.AuthRole;
import com.irummate.global.aop.RequiresAuth;
import com.irummate.global.response.GlobalApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class MatchingConfigController {

    private final MatchingConfigService matchingConfigService;

    @Autowired
    public MatchingConfigController(MatchingConfigService matchingConfigService) {
        this.matchingConfigService = matchingConfigService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 날짜 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "매칭 날짜 설정이 존재하지 않음")
    })
    @GetMapping("/match/config")
    public ResponseEntity<GlobalApiResponse<MatchingConfigDto>> getMatchDate(@AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,
                "매칭 날짜 조회 성공",
                matchingConfigService.getMatchDate()));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 날짜 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값. 시작일과 종료일은 필수이며, 시작일은 종료일보다 늦을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @RequiresAuth(roles = AuthRole.ADMIN)
    @PatchMapping("/match/config")
    public ResponseEntity<GlobalApiResponse<?>> setMatchDate(@AuthenticationPrincipal Long userId,
                                                             @Valid @RequestBody MatchingConfigDto matchingConfigDto) {
        matchingConfigService.setMatchDate(matchingConfigDto.getMatchStartDate(),
                matchingConfigDto.getMatchEndDate());

        return ResponseEntity.ok(GlobalApiResponse.success(HttpStatus.OK,
                "매칭 날짜 설정 성공",
                null));
    }
}
