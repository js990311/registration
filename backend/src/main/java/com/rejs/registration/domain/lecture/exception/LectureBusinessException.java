package com.rejs.registration.domain.lecture.exception;

import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;

public class LectureBusinessException extends BusinessException {
    public LectureBusinessException(ProblemCode code, String detail) {
        super(code, detail);
    }

    public LectureBusinessException(ProblemCode code) {
        super(code);
    }

    public static LectureBusinessException lectureNotFound(){
        return new LectureBusinessException(ProblemCode.LECTURE_NOT_FOUND);
    }

    public static LectureBusinessException lectureNotFound(String detail){
        return new LectureBusinessException(ProblemCode.LECTURE_NOT_FOUND, detail);
    }

}
