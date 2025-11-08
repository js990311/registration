package com.rejs.registration.domain.registration.exception;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;

public class RegistrationBusinessException extends BusinessException {
    public RegistrationBusinessException(ProblemCode code, String detail) {
        super(code, detail);
    }

    public RegistrationBusinessException(ProblemCode code) {
        super(code);
    }


    public static RegistrationBusinessException notRegistrationPeriod(){
        return new RegistrationBusinessException(ProblemCode.NOT_REGISTRATION_PERIOD);
    }

    public static RegistrationBusinessException notRegistrationPeriod(String detail){
        return new RegistrationBusinessException(ProblemCode.NOT_REGISTRATION_PERIOD, detail);
    }

    public static RegistrationBusinessException lectureAlreadyFull(){
        return new RegistrationBusinessException(ProblemCode.LECTURE_ALREADY_FULL);
    }

    public static RegistrationBusinessException lectureAlreadyFull(String detail){
        return new RegistrationBusinessException(ProblemCode.LECTURE_ALREADY_FULL, detail);
    }

    public static RegistrationBusinessException alreadyRegistration(){
        return new RegistrationBusinessException(ProblemCode.ALREADY_REGISTRATION);
    }

    public static RegistrationBusinessException alreadyRegistration(String detail){
        return new RegistrationBusinessException(ProblemCode.ALREADY_REGISTRATION, detail);
    }
}
