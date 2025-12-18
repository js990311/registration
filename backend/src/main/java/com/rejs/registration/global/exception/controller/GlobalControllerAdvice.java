package com.rejs.registration.global.exception.controller;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.registration.global.response.ExceptionDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDetail> authenticationException(AuthenticationException ex, HttpServletRequest request){
        ProblemCode code = ProblemCode.INVALID_TOKEN;
        return ResponseEntity.status(code.getStatus()).body(ExceptionDetail.of(code, request.getRequestURI(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDetail> accessDeniedException(AccessDeniedException ex, HttpServletRequest request){
        ProblemCode code = ProblemCode.ACCESS_DENIED;
        return ResponseEntity.status(code.getStatus()).body(ExceptionDetail.of(code, request.getRequestURI(), null));
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ExceptionDetail> handleBusinessException(BusinessException ex , HttpServletRequest request){
        log.info("[BusinessProblem] {} in {} detail : {}",
                ex.getCode().getTitle(),
                request.getRequestURI(),
                ex.getDetail());
        return ResponseEntity.status(ex.getCode().getStatus()).body(ExceptionDetail.of(ex.getCode(), request.getRequestURI(), ex.getDetail()));
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<ExceptionDetail> noHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request){
        ProblemCode code = ProblemCode.NOT_FOUNT;
        return ResponseEntity.status(code.getStatus()).body(ExceptionDetail.of(code, request.getRequestURI(), null));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ExceptionDetail> handleBaseException(RuntimeException ex, HttpServletRequest request){
        log.warn("[Unexpected Server Exception] ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionDetail.of(ProblemCode.INTERNAL_SERVER_ERROR, request.getRequestURI(), null));
    }
}
