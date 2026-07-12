package com.irummate.domain.user.service;

import com.irummate.domain.user.dto.UserDetailsRequestDto;
import com.irummate.domain.user.dto.UserDetailsResponseDto;
import com.irummate.domain.user.dto.UserProfileResponseDto;
import com.irummate.domain.user.dto.UserProfileUpdateRequestDto;
import com.irummate.domain.user.dto.UserProfileUpdateResponseDto;
import com.irummate.domain.user.entity.UserDetails;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UserDetailsRepository;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsService {

    private final UsersRepository usersRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Transactional
    public UserDetailsResponseDto createDetails(Long userId, UserDetailsRequestDto request) {
        Long userPk = Long.valueOf(userId);

        if (userDetailsRepository.existsById(userPk)) {
            throw new BusinessException(ErrorCode.DETAILS_ALREADY_EXISTS);
        }

        Users user = usersRepository.findById(userPk)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = UserDetails.builder()
                .user(user)
                .realName(request.getRealName())
                .studentId(request.getStudentId())
                .age(request.getAge())
                .gender(request.getGender())
                .department(request.getDepartment())
                .phoneNumber(request.getPhoneNumber())
                .build();

        UserDetails savedDetails = userDetailsRepository.save(userDetails);
        user.promoteToUser();

        return toResponse(savedDetails);
    }

    public UserProfileResponseDto getProfile(Long userId) {
        Long userPk = Long.valueOf(userId);

        Users user = usersRepository.findById(userPk)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = userDetailsRepository.findById(userPk).orElse(null);

        return UserProfileResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .detail(toProfileDetail(userDetails))
                .build();
    }

    @Transactional
    public UserProfileUpdateResponseDto updateProfile(Long userId, UserProfileUpdateRequestDto request) {
        Long userPk = Long.valueOf(userId);

        Users user = usersRepository.findById(userPk)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(request.getNickname(), request.getProfileImageUrl());

        return UserProfileUpdateResponseDto.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    private UserDetailsResponseDto toResponse(UserDetails userDetails) {
        return UserDetailsResponseDto.builder()
                .realName(userDetails.getRealName())
                .studentId(userDetails.getStudentId())
                .age(userDetails.getAge())
                .gender(userDetails.getGender())
                .department(userDetails.getDepartment())
                .phoneNumber(userDetails.getPhoneNumber())
                .build();
    }

    private UserProfileResponseDto.Detail toProfileDetail(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        return UserProfileResponseDto.Detail.builder()
                .realName(userDetails.getRealName())
                .studentId(userDetails.getStudentId())
                .age(userDetails.getAge())
                .gender(userDetails.getGender())
                .department(userDetails.getDepartment())
                .phoneNumber(userDetails.getPhoneNumber())
                .build();
    }
}
