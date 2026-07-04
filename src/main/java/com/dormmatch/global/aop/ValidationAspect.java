package com.dormmatch.global.aop;

import com.dormmatch.domain.survey.repository.UserPreferencesRepository;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ValidationAspect {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ValidationAspect(
            UserPreferencesRepository userPreferencesRepository,
            UsersRepository usersRepository
    ) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.usersRepository = usersRepository;
    }

    @Before("@annotation(com.dormmatch.global.aop.RequiresSurvey)")
    public void checkSurveyCompleted(JoinPoint joinPoint) {
        Long userId = extractUserId(joinPoint);

        Boolean surveyCompleted = userPreferencesRepository
                .findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SURVEY_NOT_FOUND))
                .getIsCompleted();

        if (Boolean.FALSE.equals(surveyCompleted)) {
            throw new BusinessException(ErrorCode.SURVEY_REQUIRED);
        }
    }

    @Before("@annotation(requiresAuth)")
    public void checkAuth(JoinPoint joinPoint, RequiresAuth requiresAuth) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        log.debug("[AuthAspect] userId: {}, method: {}", userId, joinPoint.getSignature().getName());

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        AuthRole[] authRoles = requiresAuth.roles();
        if (authRoles.length == 0) {
            return;
        }

        boolean hasRole = Arrays.stream(authRoles)
                .anyMatch(role -> role.name().equals(user.getRole()));

        if (!hasRole) {
            log.warn("[AuthAspect] forbidden - userId: {}, role: {}, required: {}",
                    userId, user.getRole(), Arrays.toString(authRoles));
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    @Before("@annotation(com.dormmatch.global.aop.RequiresCertification)")
    public void checkCertification(JoinPoint joinPoint) {
        extractUserId(joinPoint);
    }

    public static Long extractUserId(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Long userId) {
                return userId;
            }
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
}
