package com.dormmatch.global.exception;


import com.dormmatch.global.response.GlobalApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ApiResponses(
            value = @ApiResponse(
                responseCode = "500", description = "INTERNAL_SERVER_ERROR"
            )
    )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<?>> handleException(Exception e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(GlobalApiResponse.error(status, "서버 내부 오류입니다."));
    }
}
