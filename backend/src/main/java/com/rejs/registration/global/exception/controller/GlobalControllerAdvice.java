package com.rejs.registration.global.exception.controller;

import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(GlobalException ex){
        return ResponseEntity.status(ex.getStatus()).body(BaseResponse.fromException(ex));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, null));
    }
}
