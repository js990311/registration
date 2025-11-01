package com.rejs.registration.domain.lecture.service;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.dto.LectrueDto;
import com.rejs.registration.domain.lecture.dto.request.CreateLectureRequest;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public LectrueDto create(CreateLectureRequest request) {
        Lecture lecture = new Lecture(request.getName(), request.getCapacity());
        lecture = lectureRepository.save(lecture);
        return LectrueDto.from(lecture);
    }


    public LectrueDto findById(Long id) {
        Lecture lecture = lectureRepository.findById(id).orElseThrow(NotFoundException::lectureNotFound);
        return LectrueDto.from(lecture);
    }
}
