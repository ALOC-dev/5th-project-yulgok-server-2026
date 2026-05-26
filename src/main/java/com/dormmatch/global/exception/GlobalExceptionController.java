package com.dormmatch.global.exception;

import com.dormmatch.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "INTERNAL_SERVER_ERROR"
    )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        // 서버 콘솔에 진짜 에러 내용과 추적 이력(StackTrace)을 남겨줍니다. (버그 잡기 필수!)
        log.error("서버 내부 오류 발생: ", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // 우리가 만든 ApiResponse 규격에 맞춰 에러 객체를 담아 리턴합니다.
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status, "서버 내부 오류가 발생하였습니다."));
    }
}