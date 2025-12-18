package com.rejs.registration.global.response;

import lombok.Getter;

@Getter
public class ApiExceptionResponse {
    private final ExceptionDetail error;

    public ApiExceptionResponse(ExceptionDetail error) {
        this.error = error;
    }
}
