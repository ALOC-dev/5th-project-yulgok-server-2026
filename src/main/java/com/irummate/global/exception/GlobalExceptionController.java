package com.irummate.global.exception;

import com.irummate.global.response.GlobalApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<?>> handleException(Exception e) {
        log.warn("Internal Server Error: {}", e.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(status)
                .body(GlobalApiResponse.error(status, "서버 내부 오류입니다.", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());

        List<GlobalApiResponse.ErrorResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new GlobalApiResponse.ErrorResponse(
                        fe.getField(),
                        fe.getDefaultMessage()
                ))
                .toList();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(GlobalApiResponse.error(status, "필수 항목 누락 또는 값 범위 초과", errors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GlobalApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCode.getHttpStatus();

        return ResponseEntity
                .status(httpStatus)
                .body(GlobalApiResponse.error(httpStatus, e.getMessage(), null));
    }
}
