package com.rejs.registration.global.problem;

import org.springframework.http.HttpStatus;

public enum ProblemCode {
    // 인증 관랸
    INVALID_TOKEN("INVALID_TOKEN", "잘못된 토큰입니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "권한이 없습니다", HttpStatus.FORBIDDEN),
    USER_INFO_MISMATCH("USER_INFO_MISMATCH", "유저정보가 맞지 않습니다", HttpStatus.UNAUTHORIZED),

    // Student 관련
    STUDENT_NOT_FOUND("STUDENT_NOT_FOUND", "존재하지 않는 학생입니다", HttpStatus.NOT_FOUND),

    // Lecture 관련
    LECTURE_NOT_FOUND("LECTURE_NOT_FOUND", "없는 강의입니다.", HttpStatus.NOT_FOUND),

    // 등록 관련
    LECTURE_ALREADY_FULL("LECTURE_ALREADY_FULL", "해당 강의의 인원이 초과되었습니다.", HttpStatus.CONFLICT),
    ALREADY_REGISTRATION("ALREADY_REGISTRATION", "이미 수강신청한 강의입니다.", HttpStatus.CONFLICT),
    NOT_REGISTRATION_PERIOD("NOT_REGISTRATION_PERIOD", "수강신청 기한이 아닙니다", HttpStatus.FORBIDDEN),
    REGISTRATION_NOT_FOUND("REGISTRATION_NOT_FOUND", "존재하지 않는 수강신청 내역입니다", HttpStatus.NOT_FOUND),
    // 서버 에러
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "알 수 없는 서버 문제가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    ProblemCode(String type, String title, HttpStatus status) {
        this.type = type;
        this.title = title;
        this.status = status;
    }

    private String type;
    private String title;
    private HttpStatus status;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
