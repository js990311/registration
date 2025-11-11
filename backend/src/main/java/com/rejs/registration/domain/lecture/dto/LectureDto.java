package com.rejs.registration.domain.lecture.dto;

import com.rejs.registration.domain.entity.Lecture;
import lombok.Getter;

@Getter
public class LectureDto {
    private Long lectureId;
    private String name;
    private Integer capacity;

    public LectureDto(Long lectureId, String name, Integer capacity) {
        this.lectureId = lectureId;
        this.name = name;
        this.capacity = capacity;
    }

    public static LectureDto from(Lecture lecture) {
        return new LectureDto(
                lecture.getId(),
                lecture.getName(),
                lecture.getCapacity()
        );
    }
}
