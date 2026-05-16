//package com.dormmatch.global.exception;
//
//
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionController {
//
//    @ApiResponses(
//            value = @ApiResponse(
//                responseCode = "500", description = "INTERNAL_SERVER_ERROR"
//            )
//    )
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleException(Exception e){
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        return new ResponseEntity<>(com.dormmatch.global.response.ApiResponse.error(status, "서버 내부 오류가 발생하였습니다."));
//    }
//
//}
