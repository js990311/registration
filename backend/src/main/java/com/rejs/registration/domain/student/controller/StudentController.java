package com.rejs.registration.domain.student.controller;

import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.domain.student.dto.request.CreateStudentRequest;
import com.rejs.registration.domain.student.service.StudentService;
import com.rejs.registration.global.authentication.claims.annotation.TokenClaim;
import com.rejs.token_starter.token.ClaimsDto;
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

    @GetMapping("/me")
    public StudentDto getById(@TokenClaim ClaimsDto claims){
        StudentDto student = studentService.findById(Long.valueOf(claims.getUsername()));
        return student;
    }

}
