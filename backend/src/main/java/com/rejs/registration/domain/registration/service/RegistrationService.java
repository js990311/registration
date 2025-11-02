package com.rejs.registration.domain.registration.service;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.lecture.service.LectureService;
import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public CreateRegistrationResponse create(Long studentId, CreateRegistrationRequest request) {
        Lecture lecture = lectureRepository.findById(request.getLectureId()).orElseThrow(NotFoundException::lectureNotFound);
        Student student = studentRepository.findById(studentId).orElseThrow(()->new GlobalException(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED));
        Registration registration = new Registration(student, lecture);

        // 중복 신청 여부 확인
        if(registrationRepository.isAlreadyRegistered(lecture.getId(), student.getId())){
            throw new GlobalException("Lecture already registered", HttpStatus.CONFLICT);
        }

        // capacity 초과 검사
        Long count = registrationRepository.countByLectureId(lecture.getId());
        if (count >= lecture.getCapacity()){
            throw new GlobalException("Lecture is already full", HttpStatus.CONFLICT);
        }

        registration = registrationRepository.save(registration);
        return CreateRegistrationResponse.from(registration);
    }
}
