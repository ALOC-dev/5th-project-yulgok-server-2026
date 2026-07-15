package com.irummate.domain.admin.dto;

import com.irummate.domain.user.entity.Users;
import com.irummate.global.util.HashIdsUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminUserResponseDto {

    private String userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    public static AdminUserResponseDto from(Users user) {
        return new AdminUserResponseDto(
                HashIdsUtils.encode(user.getId()),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}
