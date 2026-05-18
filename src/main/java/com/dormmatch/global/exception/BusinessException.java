package com.dormmatch.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String coustomMessage){
        super(coustomMessage);
        this.errorCode = errorCode;
    }
}
