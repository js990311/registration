package com.rejs.registration.domain.lecture.service;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.dto.LectureDto;
import com.rejs.registration.domain.lecture.dto.request.CreateLectureRequest;
import com.rejs.registration.domain.lecture.exception.LectureBusinessException;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public LectureDto create(CreateLectureRequest request) {
        Lecture lecture = new Lecture(request.getName(), request.getCapacity());
        lecture = lectureRepository.save(lecture);
        return LectureDto.from(lecture);
    }


    public LectureDto findById(Long id) {
        Lecture lecture = lectureRepository.findById(id).orElseThrow(LectureBusinessException::lectureNotFound);
        return LectureDto.from(lecture);
    }

    public Page<LectureDto> findLectures(Pageable pageable) {
        return lectureRepository.findAll(pageable).map(LectureDto::from);
    }
}
