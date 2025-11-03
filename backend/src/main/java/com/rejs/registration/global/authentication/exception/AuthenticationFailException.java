package com.rejs.registration.global.authentication.exception;

import com.rejs.registration.global.exception.GlobalException;
import org.springframework.http.HttpStatus;

public class AuthenticationFailException extends GlobalException {

    public AuthenticationFailException(String message, HttpStatus status) {
        super(message, status);
    }

    public static AuthenticationFailException authenticationFail(){
        return new AuthenticationFailException("AuthenticationFail", HttpStatus.UNAUTHORIZED);
    }
    public static AuthenticationFailException userInfoMismatch(){
        return new AuthenticationFailException("UserInfoMismatch", HttpStatus.UNAUTHORIZED);
    }

    public static AuthenticationFailException invalidToken(){
        return new AuthenticationFailException("InvalidToken", HttpStatus.UNAUTHORIZED);
    }

}
