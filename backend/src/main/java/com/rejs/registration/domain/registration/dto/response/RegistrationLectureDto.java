package com.rejs.registration.domain.registration.dto.response;

import lombok.Getter;

@Getter
public class RegistrationLectureDto {
    private Long lectureId;
    private String name;
    private Integer capacity;
    private Long registrationId;

    public RegistrationLectureDto(Long lectureId, String name, Integer capacity, Long registrationId) {
        this.lectureId = lectureId;
        this.name = name;
        this.capacity = capacity;
        this.registrationId = registrationId;
    }
}
