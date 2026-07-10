package com.irummate.global.aop;

import com.irummate.domain.survey.entity.UserPreferences;
import com.irummate.domain.survey.repository.UserPreferencesRepository;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ValidationAspect {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ValidationAspect(UserPreferencesRepository userPreferencesRepository,
                            UsersRepository usersRepository){
        this.userPreferencesRepository = userPreferencesRepository;
        this.usersRepository = usersRepository;
    }

    // survey가 완료된 상태인지 검증
    // @RequiresSurvey를 메서드 앞에 붙여서 사용
    @Before("@annotation(com.dormmatch.global.aop.RequiresSurvey)")
    public void checkSurveyCompleted(JoinPoint joinPoint){
        Long userId = extractUserId(joinPoint);

        boolean surveyCompleted = userPreferencesRepository
                .findByUserId(userId)
                .map(UserPreferences::getIsCompleted)
                .orElse(false);

        if (!surveyCompleted) {
            throw new BusinessException(ErrorCode.SURVEY_REQUIRED);
        }
    }


    // 인증서 인증
    // @RequiresCertification
    @Before("@annotation(com.dormmatch.global.aop.RequiresCertification)")
    public void checkCertification(JoinPoint joinPoint){
        Long userId = extractUserId(joinPoint);


    }

    // 매개변수로 들어오는 userId 확인
    public static Long extractUserId(JoinPoint joinPoint){
        for(Object args : joinPoint.getArgs()) {
            if (args instanceof Long) {
                return (Long) args;
            }
        }
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
}
