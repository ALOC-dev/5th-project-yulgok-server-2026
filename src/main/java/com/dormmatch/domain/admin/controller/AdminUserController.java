package com.dormmatch.domain.admin.controller;

import com.dormmatch.domain.admin.dto.AdminUserResponseDto;
import com.dormmatch.domain.admin.service.AdminUserService;
import com.dormmatch.global.aop.AuthRole;
import com.dormmatch.global.aop.RequiresAuth;
import com.dormmatch.global.response.GlobalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<List<AdminUserResponseDto>>> getUsers() {
        List<AdminUserResponseDto> responseDtos = adminUserService.getUsers();

        return ResponseEntity.ok(
                GlobalApiResponse.multiSuccess(HttpStatus.OK, "회원 목록 조회 성공", responseDtos, responseDtos.size())
        );
    }

    @PatchMapping("/{userId}/ban")
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<AdminUserResponseDto>> banUser(
            @PathVariable Long userId
    ) {
        AdminUserResponseDto responseDto = adminUserService.banUser(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "회원 정지 성공", responseDto)
        );
    }

    @PatchMapping("/{userId}/unban")
    @RequiresAuth(roles = AuthRole.ADMIN)
    public ResponseEntity<GlobalApiResponse<AdminUserResponseDto>> unbanUser(
            @PathVariable Long userId
    ) {
        AdminUserResponseDto responseDto = adminUserService.unbanUser(userId);

        return ResponseEntity.ok(
                GlobalApiResponse.success(HttpStatus.OK, "회원 정지 해제 성공", responseDto)
        );
    }
}
