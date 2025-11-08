package com.rejs.registration.domain.student.exception;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;

public class StudentBusinessException extends BusinessException {

    public StudentBusinessException(ProblemCode code, String detail) {
        super(code, detail);
    }

    public StudentBusinessException(ProblemCode code) {
        super(code);
    }

    public static StudentBusinessException studentNotFound(){
        return new StudentBusinessException(ProblemCode.STUDENT_NOT_FOUND);
    }

    public static StudentBusinessException studentNotFound(String detail){
        return new StudentBusinessException(ProblemCode.STUDENT_NOT_FOUND, detail);
    }

}
