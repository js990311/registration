package com.rejs.registration.domain.lecture.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CreateLectureRequest {
    private String name;
    private Integer capacity;
    private Integer credit;
}
