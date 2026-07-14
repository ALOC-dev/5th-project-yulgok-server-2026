package com.irummate.domain.admin.service;

import com.irummate.domain.admin.dto.AdminUserResponseDto;
import com.irummate.domain.user.entity.Users;
import com.irummate.domain.user.repository.UsersRepository;
import com.irummate.global.exception.BusinessException;
import com.irummate.global.exception.ErrorCode;
import com.irummate.global.util.HashIdsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<AdminUserResponseDto> getUsers() {
        return usersRepository.findAll()
                .stream()
                .map(AdminUserResponseDto::from)
                .toList();
    }

    @Transactional
    public AdminUserResponseDto banUser(String userId) {
        Users user = getUser(decodeUserId(userId));

        user.ban();

        return AdminUserResponseDto.from(user);
    }

    @Transactional
    public AdminUserResponseDto unbanUser(String userId) {
        Users user = getUser(decodeUserId(userId));

        user.unban();

        return AdminUserResponseDto.from(user);
    }

    private Users getUser(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Long decodeUserId(String userId) {
        try {
            return HashIdsUtils.decode(userId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
