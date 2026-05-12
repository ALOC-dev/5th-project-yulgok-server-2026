package com.dormmatch.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.http.HttpStatusCode;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final T data;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static ApiResponse<?> error(HttpStatus status, String message){
        return ApiResponse.builder()
                .error(new ErrorResponse(status.value(), status.name(), message))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse{
        private final int code;
        private final String error;
        private final String message;
    }
}
