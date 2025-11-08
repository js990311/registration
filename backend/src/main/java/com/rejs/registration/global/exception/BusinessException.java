package com.rejs.registration.global.exception;

import com.rejs.registration.global.problem.ProblemCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private ProblemCode code;
    private String detail;

    public BusinessException(ProblemCode code, String detail) {
        super(detail);
        this.code = code;
        this.detail = detail;
    }

    public BusinessException(ProblemCode code) {
        super(code.getTitle());
        this.code = code;
    }
}
