package com.dormmatch.domain.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@Getter
public class UsersRequestDto {
    @NonNull    private String oauthid;
    @NonNull    private String nickname;
                private String email;
                private String profileImageUrl;

    // 추가 정보 (user_details)
    @NonNull    private String realName;
    @NonNull    private String studentId;
    @Min(17)    @Max(40)    private int    age;
    @NonNull    private String gender;
    @NonNull    private String department;
}
