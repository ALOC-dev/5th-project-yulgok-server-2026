package com.irummate.domain.matching.service;

import com.irummate.domain.matching.entity.MatchingConfig;
import com.irummate.domain.matching.repository.MatchingConfigRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MatchingConfigService {

    private final MatchingConfigRepository matchingConfigRepository;

    @Autowired
    public MatchingConfigService(MatchingConfigRepository matchingConfigRepository){
        this.matchingConfigRepository = matchingConfigRepository;
    }


    @Transactional
    public void setMatchDate(LocalDate matchDate){

        MatchingConfig config = matchingConfigRepository.findById(MatchingConfig.SINGLETON_ID)
                .orElseGet(()->new MatchingConfig(matchDate));

        config.updateMatchDate(matchDate);
        matchingConfigRepository.save(config);

    }

    @Transactional(readOnly = true)
    public LocalDate getMatchDate(){
        MatchingConfig config = matchingConfigRepository.findById(MatchingConfig.SINGLETON_ID)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST));

        return config.getMatchDate();
    }


}


