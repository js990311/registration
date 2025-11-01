package com.rejs.registration.domain.student.dto;

import com.rejs.registration.domain.entity.Student;
import lombok.Getter;

@Getter
public class StudentDto {
    private Long studentId;
    private String name;

    public StudentDto(Long studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public static StudentDto from(Student student){
        return new StudentDto(student.getId(), student.getName());
    }
}
