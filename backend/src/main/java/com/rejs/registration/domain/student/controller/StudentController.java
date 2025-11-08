package com.rejs.registration.domain.student.controller;

import com.rejs.registration.domain.lecture.dto.LectrueDto;
import com.rejs.registration.domain.lecture.dto.request.CreateLectureRequest;
import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.domain.student.dto.request.CreateStudentRequest;
import com.rejs.registration.domain.student.service.StudentService;
import com.rejs.registration.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/students")
@RestController
public class StudentController {
    private final StudentService studentService;

    @Deprecated
    @PostMapping
    public ResponseEntity<StudentDto> createLecture(@RequestBody CreateStudentRequest request){
        StudentDto student = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @GetMapping("/{id}")
    public StudentDto getById(@PathVariable("id") Long id){
        StudentDto student = studentService.findById(id);
        return student;
    }

}
