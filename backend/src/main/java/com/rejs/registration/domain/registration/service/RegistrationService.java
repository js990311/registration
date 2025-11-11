package com.rejs.registration.domain.registration.service;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.exception.LectureBusinessException;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.dto.response.RegistrationLectureDto;
import com.rejs.registration.domain.registration.exception.RegistrationBusinessException;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.exception.StudentBusinessException;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.exception.NotFoundException;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.registration.global.response.PageResponse;
import com.rejs.token_starter.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Lecture lecture = lectureRepository.findByIdWithLock(request.getLectureId()).orElseThrow(LectureBusinessException::lectureNotFound);
        Student student = studentRepository.findById(Long.parseLong(claims.getUsername())).orElseThrow(StudentBusinessException::studentNotFound);

        if(registrationRepository.isAlreadyRegistered(lecture.getId(), student.getId())){
            throw RegistrationBusinessException.alreadyRegistration();
        }

        // 강의가 여전히 신청가능한지 확인
        Long studentCount = registrationRepository.countByLectureId(lecture.getId());
        if(studentCount >= lecture.getCapacity()){
            throw RegistrationBusinessException.lectureAlreadyFull("현 수강인원 : " + studentCount);
        }

        Registration registration = new Registration(student, lecture);
        registration = registrationRepository.save(registration);

        return CreateRegistrationResponse.from(registration);
    }

    public boolean validateRegistarionPeriod(LocalDateTime now){
        List<RegistrationPeriod> periods = periodRepository.findByPeroid(now);
        // 나중에 1학년만 가능 등등 옵션 추가가능할 수 있도록
        return !periods.isEmpty();
    }

    @Transactional
    public void delete(ClaimsDto claims, Long id) {
        Registration registration = registrationRepository.findByIdWithLock(id).orElseThrow(RegistrationBusinessException::registrationNotFound);
        if(!registration.getStudentId().toString().equals(claims.getUsername())){
            throw new BusinessException(ProblemCode.ACCESS_DENIED, "본인의 수강신청내역만 취소할 수 있습니다");
        }
        registrationRepository.delete(registration);
    }


    @Transactional(readOnly = true)
    public Page<RegistrationLectureDto> findByStudentId(ClaimsDto claims, Pageable pageable) {
        return registrationRepository.findByStudentId(Long.valueOf(claims.getUsername()), pageable);
    }
}
