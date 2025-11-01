package com.rejs.registration.domain.student.service;

import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.domain.student.dto.request.CreateStudentRequest;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public StudentDto create(CreateStudentRequest request) {
        Student student = new Student(request.getName());
        student = studentRepository.save(student);
        return StudentDto.from(student);
    }


    public StudentDto findById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::studentNotFound);
        return StudentDto.from(student);
    }

}
