package com.rejs.registration.domain.lecture.controller;

import com.rejs.registration.domain.lecture.dto.LectureDto;
import com.rejs.registration.domain.lecture.dto.request.CreateLectureRequest;
import com.rejs.registration.domain.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LectureDto createLecture(@RequestBody CreateLectureRequest request){
        LectureDto lectureDto = lectureService.create(request);
        return lectureDto;
    }

    @GetMapping("/{id}")
    public LectureDto getById(@PathVariable("id") Long id){
        LectureDto lectureDto = lectureService.findById(id);
        return lectureDto;
    }

    @GetMapping
    public Page<LectureDto> getLectures(
            @PageableDefault Pageable pageable
            ){
        return lectureService.findLectures(pageable);
    }

}
