package com.dormmatch.global.scheduler;

import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class MatchingScheduler {

    private final MatchingSchedulerService matchingSchedulerService;

    @Autowired
    public MatchingScheduler(MatchingSchedulerService matchingSchedulerService){
        this.matchingSchedulerService = matchingSchedulerService;
    }

    @Scheduled(cron = "0 15 0 * * *")
    public void runDailyMatching() {

        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2027, 1, 3);

        if(now.isBefore(startDate) || now.isAfter(endDate))  return;

        log.info("Daily matching process started at {}", LocalDate.now());

        matchingSchedulerService.processMatching();

        log.info("Daily matching process completed at {}", LocalDate.now());

    }
}
