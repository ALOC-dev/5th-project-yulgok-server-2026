package com.dormmatch.global.scheduler;

import com.dormmatch.domain.matching.entity.MatchRequests;
import com.dormmatch.domain.matching.entity.MatchStatus;
import com.dormmatch.domain.matching.repository.MatchRepository;
import com.dormmatch.domain.survey.entity.UserPreferences;
import com.dormmatch.domain.survey.repository.UserPreferencesRepository;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.pgvector.PGvector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingSchedulerService {

    private final MatchRepository matchRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UsersRepository userRepository;

    @Autowired
    public MatchingSchedulerService(MatchRepository matchRepository,
                                    UserPreferencesRepository userPreferencesRepository,
                                    UsersRepository userRepository){
        this.matchRepository = matchRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void processMatching() {

    }

    private float[] toFloatArray(double[] arr) {
        float[] result = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (float) arr[i];
        }
        return result;
    }


    private double getDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.round(Math.sqrt(sum)*10)/10.0;
    }
}
