package com.rejs.registration.domain.lecture.dto;

import com.rejs.registration.domain.entity.Lecture;
import lombok.Getter;

@Getter
public class LectrueDto {
    private Long lectureId;
    private String name;
    private Integer capacity;

    public LectrueDto(Long lectureId, String name, Integer capacity) {
        this.lectureId = lectureId;
        this.name = name;
        this.capacity = capacity;
    }

    public static LectrueDto from(Lecture lecture) {
        return new LectrueDto(
                lecture.getId(),
                lecture.getName(),
                lecture.getCapacity()
        );
    }
}
