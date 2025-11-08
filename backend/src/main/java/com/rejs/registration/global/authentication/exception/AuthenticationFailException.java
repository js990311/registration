package com.rejs.registration.global.authentication.exception;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.problem.ProblemCode;
import org.springframework.http.HttpStatus;


public class AuthenticationFailException extends BusinessException {

    public AuthenticationFailException(ProblemCode code, String detail) {
        super(code, detail);
    }

    public AuthenticationFailException(ProblemCode code) {
        super(code);
    }

    public static AuthenticationFailException userInfoMismatch(){
        return new AuthenticationFailException(ProblemCode.USER_INFO_MISMATCH);
    }

    public static AuthenticationFailException userInfoMismatch(String detail){
        return new AuthenticationFailException(ProblemCode.USER_INFO_MISMATCH, detail);
    }

}
