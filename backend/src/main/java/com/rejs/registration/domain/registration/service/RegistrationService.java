package com.rejs.registration.domain.registration.service;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.lecture.service.LectureService;
import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.exception.NotFoundException;
import com.rejs.token_starter.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public CreateRegistrationResponse create(ClaimsDto claims, CreateRegistrationRequest request) {
        if(!validateRegistarionPeriod(LocalDateTime.now())){
            throw new GlobalException("Not RegistrationPeriod", HttpStatus.FORBIDDEN);
        }
        Lecture lecture = lectureRepository.findByIdWithLock(request.getLectureId()).orElseThrow(NotFoundException::lectureNotFound);
        Student student = studentRepository.findById(Long.parseLong(claims.getUsername())).orElseThrow(()->new GlobalException(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED));

        // 중복 신청여부 확인
        if(registrationRepository.isAlreadyRegistered(lecture.getId(), student.getId())){
            throw new GlobalException("Lecture already registered", HttpStatus.CONFLICT);
        }

        // 강의가 여전히 신청가능한지 확인
        if(!lecture.hasCapacity()){
            throw new GlobalException("Lecture already registered", HttpStatus.CONFLICT);
        }

        // registration 생성 -> 이때 lecture entity의 increaseStudent를 Registration의 생성자 내에서 호출
        Registration registration = new Registration(student, lecture);
        registration = registrationRepository.save(registration);
        return CreateRegistrationResponse.from(registration);
    }

    public boolean validateRegistarionPeriod(LocalDateTime now){
        List<RegistrationPeriod> periods = periodRepository.findByPeroid(now);
        // 나중에 1학년만 가능 등등 옵션 추가가능할 수 있도록
        return !periods.isEmpty();
    }
}
