package com.irummate.domain.matching.controller;

import com.irummate.domain.matching.dto.MatchingConfigResponseDto;
import com.irummate.domain.matching.service.MatchingConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class MatchingConfigController {

    private final MatchingConfigService matchingConfigService;

    @Autowired
    public MatchingConfigController(MatchingConfigService matchingConfigService){
        this.matchingConfigService = matchingConfigService;
    }

    @GetMapping("/match/config")
    public MatchingConfigResponseDto getMatchDate(){
        return null;
    }
}

