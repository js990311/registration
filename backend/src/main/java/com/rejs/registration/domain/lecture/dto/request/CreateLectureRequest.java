package com.rejs.registration.domain.lecture.dto.request;

import lombok.Getter;

@Getter
public class CreateLectureRequest {
    private String name;
    private Integer capacity;
}
