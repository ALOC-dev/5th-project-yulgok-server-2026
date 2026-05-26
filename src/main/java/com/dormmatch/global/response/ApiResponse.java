package com.dormmatch.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 응답에 포함하지 않음
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    // 생성자
    private ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "요청에 성공하였습니다.", data);
    }

    // 성공 응답 (데이터 미포함)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpStatus.OK, "요청에 성공하였습니다.", null);
    }

    // 에러 응답
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
