package com.dormmatch.domain.auth.service;

import com.dormmatch.domain.auth.dto.KakaoTokenResponseDto;
import com.dormmatch.domain.auth.dto.KakaoUserInfoResponseDto;
import com.dormmatch.domain.auth.dto.LoginResponseDto;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.config.KakaoProperties;
import com.dormmatch.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoProperties kakaoProperties;
    private final UsersRepository usersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public LoginResult loginOrRegister(String code){
        // 카카오 인가 코드를 카카오 액세스 토큰으로 교환한다.
        KakaoTokenResponseDto tokenResponse = getKakaoToken(code);

        // 카카오 액세스 토큰으로 카카오 사용자 정보를 조회한다.
        KakaoUserInfoResponseDto userInfo = getKakaoUserInfo(tokenResponse.getAccessToken());

        Users user = findOrCreateUser(userInfo);

        // 카카오 사용자를 로컬 사용자와 연결한 뒤 서비스용 JWT를 발급한다.
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getRole());
        //refresh토큰 access토큰 분리해 전달
        LoginResponseDto response = LoginResponseDto.builder()
                .userId(user.getId())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();

        return new LoginResult(response, refreshToken);
    }
    //controller에게 넘겨주는 결과상자
    public record LoginResult(
            LoginResponseDto response,
            String refreshToken
    ) {
    }
    // authid로 기존 회원찾기+ 없으면 새회원 저장
    private Users findOrCreateUser(KakaoUserInfoResponseDto userInfo) {
        String oauthId = userInfo.getId().toString();

        // oauthId는 카카오의 고유 사용자 ID이며 로그인 식별자로 사용한다.
        return usersRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    KakaoUserInfoResponseDto.KakaoAccount.Profile profile = userInfo.getKakaoAccount().getProfile();
                    Users newUser = Users.builder()
                            .oauthId(oauthId)
                            .email(userInfo.getKakaoAccount().getEmail())
                            .nickname(profile.getNickname())
                            .profileImageUrl(profile.getProfileImageUrl())
                            .build();
                    return usersRepository.save(newUser);
                });
    }

    private KakaoTokenResponseDto getKakaoToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("client_secret", kakaoProperties.getClientSecret());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(tokenUri, request, KakaoTokenResponseDto.class);
    }

    private KakaoUserInfoResponseDto getKakaoUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(userInfoUri, HttpMethod.GET, request, KakaoUserInfoResponseDto.class).getBody();
    }
}
