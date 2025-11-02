package com.rejs.registration.domain.student.dto.request;

import lombok.Getter;

@Getter
public class CreateStudentRequest {
    private String name;

    public CreateStudentRequest(String name) {
        this.name = name;
    }
}
