package com.dormmatch.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalApiResponse<T> {
    private final T data;
    private final ErrorResponse error;

    public static <T> GlobalApiResponse<T> success(T data){
        return GlobalApiResponse.<T>builder()
                .data(data)
                .build();
    }

    public static GlobalApiResponse<?> error(HttpStatus status, String message){
        return GlobalApiResponse.builder()
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
