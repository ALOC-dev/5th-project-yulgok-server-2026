package com.dormmatch.domain.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Builder
@Data
public class UsersResponseDto {
    // 사용자 기본 정보
    @NonNull    private Long   id;
    @NonNull    private String nickname;
                private String email;
                private String profileImageUrl;
                private String role;
                private  String status;

                // 추가 정보 (user_details)
    @NonNull    private String realName;
    @NonNull    private String studentId;
    @Min(17)    @Max(40)    private int    age;
    @NonNull    private String gender;
    @NonNull    private String department;
}
