package com.dormmatch.domain.auth.service;

import com.dormmatch.domain.auth.dto.AuthStatusResponseDto;
import com.dormmatch.domain.auth.dto.KakaoTokenResponseDto;
import com.dormmatch.domain.auth.dto.KakaoUserInfoResponseDto;
import com.dormmatch.domain.auth.dto.LoginResponseDto;
import com.dormmatch.domain.auth.dto.RefreshTokenResponseDto;
import com.dormmatch.domain.survey.repository.UserPreferencesRepository;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.config.KakaoProperties;
import com.dormmatch.global.exception.BusinessException;
import com.dormmatch.global.exception.ErrorCode;
import com.dormmatch.global.jwt.JwtTokenProvider;
import com.dormmatch.global.util.HashIdsUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoProperties kakaoProperties;
    private final UsersRepository usersRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 외부 API(카카오 서버)와 HTTP 통신을 주고받기 위함
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public LoginResult loginOrRegister(String code) {
        // 카카오 인증 서버에 인가 코드를 보내어 카카오 access token을 받아옵니다.
        KakaoTokenResponseDto tokenResponse = getKakaoToken(code);

        // 받은 카카오 토큰으로 카카오 사용자 프로필 정보를 조회합니다.
        KakaoUserInfoResponseDto userInfo = getKakaoUserInfo(tokenResponse.getAccessToken());

        // 카카오 회원번호를 기준으로 우리 DB의 기존 유저를 찾고, 없으면 새로 가입시킵니다.
        UserRegistration userRegistration = findOrCreateUser(userInfo);
        Users user = userRegistration.user();
        Long internalUserId = user.getId();
        String encodedUserId = HashIdsUtils.encode(internalUserId);

        // JWT에는 userId를 String subject로 통일해서 저장합니다.
        String accessToken = jwtTokenProvider.createAccessToken(encodedUserId, user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(encodedUserId, user.getRole());

        // 로그인 결과와 사용자 기본 정보를 프론트엔드에 반환합니다.
        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(userRegistration.isNewUser())
                .user(LoginResponseDto.UserInfo.builder()
                        .id(encodedUserId)
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .build())
                .build();

        return new LoginResult(response, refreshToken);
    }

    /**
     * 기존 Refresh Token을 검증한 뒤 새로운 Access Token을 생성합니다.
     */
    public RefreshTokenResponseDto refreshAccessToken(String refreshToken) {
        // 리프레시 토큰이 없거나 유효하지 않으면 재발급하지 않습니다.
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return null;
        }

        // JWT subject에서 String userId를 꺼냅니다.
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String encodedUserId = claims.getSubject();
        Long internalUserId = HashIdsUtils.decode(encodedUserId);

        // Repository 접근 시점에만 DB PK 타입인 Long으로 변환합니다.
        Users user = usersRepository.findById(Long.valueOf(internalUserId)).orElse(null);
        if (user == null) {
            return null;
        }

        // Access Token만 새로 발급합니다.
        String accessToken = jwtTokenProvider.createAccessToken(encodedUserId, user.getRole());

        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

    public AuthStatusResponseDto getCurrentUserStatus() {
        // Spring Security Context에서 현재 요청의 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 익명 사용자라면 미인증 상태로 반환합니다.
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return AuthStatusResponseDto.builder().authenticated(false).build();
        }

        // Principal에는 JwtAuthenticationFilter가 넣어둔 String userId가 들어 있습니다.
        String userId = authentication.getPrincipal().toString();
        Users user;
        try {
            // DB 조회 시점에만 Long으로 변환합니다.
            user = usersRepository.findById(Long.valueOf(userId)).orElse(null);
        } catch (NumberFormatException e) {
            return AuthStatusResponseDto.builder().authenticated(false).build();
        }

        if (user == null) {
            return AuthStatusResponseDto.builder().authenticated(false).build();
        }

        // 현재 로그인 사용자의 기본 상태와 설문 완료 여부를 반환합니다.
        return AuthStatusResponseDto.builder()
                .authenticated(true)
                .user(AuthStatusResponseDto.UserInfo.builder()
                        .id(HashIdsUtils.encode(user.getId()))
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .certificationStatus(null)
                        .surveyCompleted(isSurveyCompleted(userId))
                        .build())
                .build();
    }

    // 가입된 유저면 그대로 가져오고, 없으면 카카오 정보로 새 유저를 저장합니다.
    private UserRegistration findOrCreateUser(KakaoUserInfoResponseDto userInfo) {
        String oauthId = userInfo.getId().toString();

        Optional<Users> existingUser = usersRepository.findByOauthId(oauthId);
        if (existingUser.isPresent()) {
            // 기존 유저인 경우 isNewUser=false
            return new UserRegistration(existingUser.get(), false);
        }

        // 신규 유저인 경우 카카오 프로필 정보로 Users 엔티티를 생성합니다.
        KakaoUserInfoResponseDto.KakaoAccount.Profile profile = userInfo.getKakaoAccount().getProfile();
        Users newUser = Users.builder()
                .oauthId(oauthId)
                .email(userInfo.getKakaoAccount().getEmail())
                .nickname(profile.getNickname())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();

        return new UserRegistration(usersRepository.save(newUser), true);
    }

    // 카카오 인증 서버에 인가 코드를 보내 카카오 access token을 받아옵니다.
    private KakaoTokenResponseDto getKakaoToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 카카오 OAuth 토큰 발급 API가 요구하는 form 파라미터를 조립합니다.
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("client_secret", kakaoProperties.getClientSecret());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            // 응답이 비어 있거나 access token이 없으면 카카오 API 오류로 처리합니다.
            KakaoTokenResponseDto response = restTemplate.postForObject(tokenUri, request, KakaoTokenResponseDto.class);
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException(ErrorCode.KAKAO_API_ERROR);
            }
            return response;
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    // 카카오 access token으로 카카오 사용자 정보 API를 호출합니다.
    private KakaoUserInfoResponseDto getKakaoUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        // 카카오 access token을 Authorization Bearer 헤더에 실어 보냅니다.
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            // 카카오 응답을 사용자 정보 DTO로 매핑합니다.
            KakaoUserInfoResponseDto response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.GET,
                    request,
                    KakaoUserInfoResponseDto.class
            ).getBody();

            // 필수 식별자와 프로필 정보가 없으면 정상 로그인으로 볼 수 없습니다.
            if (response == null || response.getId() == null || response.getKakaoAccount() == null
                    || response.getKakaoAccount().getProfile() == null) {
                throw new BusinessException(ErrorCode.KAKAO_API_ERROR);
            }
            return response;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    // 설문 완료 여부 조회도 repository 접근 시점에만 Long으로 변환합니다.
    private boolean isSurveyCompleted(String userId) {
        return userPreferencesRepository.findByUserId(Long.valueOf(userId))
                .map(userPreferences -> Boolean.TRUE.equals(userPreferences.getIsCompleted()))
                .orElse(false);
    }

    // 로그인 응답과 refresh token 값을 함께 넘기기 위한 내부 결과 타입입니다.
    public record LoginResult(LoginResponseDto response, String refreshToken) {
    }

    // 유저 엔티티와 신규 가입 여부를 함께 넘기기 위한 내부 결과 타입입니다.
    private record UserRegistration(Users user, boolean isNewUser) {
    }
}
