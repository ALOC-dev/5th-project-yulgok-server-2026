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
        List<MatchRequests> matchRequestsList = matchRepository.findAllByStatus(MatchStatus.RECOMMENDED);

        matchRequestsList.forEach(matchRequests -> matchRequests.updateStatus(MatchStatus.REJECTED));

        List<UserPreferences> userPreferencesList = userPreferencesRepository.findByIsMatchedFalseAndIsCompletedTrue();

        for(UserPreferences userPreferences : userPreferencesList) {

            Users me = userPreferences.getUser();

            if(userPreferences.getIsMatched())    continue;

            String gender = me.getUserDetails().getGender();

            float[] floatVec = toFloatArray(userPreferences.getLifestyleVector());
            PGvector vector = new PGvector(floatVec);

            // 2. TOP 3 조회
            List<Long> matchedIds = userPreferencesRepository.findTop3MatchedUserIds(
                    me.getId(),
                    userPreferences.getSmokingStatus(),
                    gender,
                    vector
            );

            List<Long> finalMatchedIds = new ArrayList<Long>(matchedIds);

            if (matchedIds.size() < 3) {
                List<Long> remainedMatchedUserIds = userPreferencesRepository.findRemainedMatchedUserIds(
                        me.getId(),
                        finalMatchedIds.isEmpty() ? List.of(-1L):matchedIds,
                        3 - matchedIds.size(),
                        gender,
                        vector
                );

                finalMatchedIds.addAll(remainedMatchedUserIds);
            }

            if(finalMatchedIds.isEmpty()) continue;

            // 3. MatchRequests 생성
            List<MatchRequests> requests = finalMatchedIds.stream()
                    .map(receiverId -> {
                        UserPreferences receiverPrefs = userPreferencesRepository
                                .findByUserId(receiverId).get();

                        return MatchRequests.builder()
                                .sender(me)
                                .receiver(receiverPrefs.getUser())
                                .status(MatchStatus.RECOMMENDED)
                                .senderPreferences(userPreferences)
                                .receiverPreferences(receiverPrefs)
                                .matchPercentage(getDistance(
                                        userPreferences.getLifestyleVector(),
                                        receiverPrefs.getLifestyleVector()
                                ))
                                .build();
                    })
                    .toList();

            matchRepository.saveAll(requests);
        }

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
