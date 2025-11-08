package com.rejs.registration.global.exception.controller;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.registration.global.response.BaseResponse;
import com.rejs.registration.global.response.ProblemResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ProblemResponse> handleBusinessException(BusinessException ex , HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        sb.append("[BusinessProblem] ")
                .append(ex.getCode().getTitle())
                .append(" in ")
                .append(request.getRequestURI())
                .append(" detail : ")
                .append(ex.getDetail());
        log.info(sb.toString());
        return ResponseEntity.status(ex.getCode().getStatus()).body(new ProblemResponse(ex.getCode(), request.getRequestURI(), ex.getDetail()));
    }

    @Deprecated
    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(GlobalException ex){
        log.info("[GlobalException] ", ex);
        return ResponseEntity.status(ex.getStatus()).body(BaseResponse.fromException(ex));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ProblemResponse> handleBaseException(RuntimeException ex, HttpServletRequest request){
        log.warn("[Unexpected Server Exception] ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ProblemResponse(ProblemCode.INTERNAL_SERVER_ERROR, request.getRequestURI(), null));
    }
}
