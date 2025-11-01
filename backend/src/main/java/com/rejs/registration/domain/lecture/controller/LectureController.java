package com.rejs.registration.domain.lecture.controller;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.dto.LectrueDto;
import com.rejs.registration.domain.lecture.dto.request.CreateLectureRequest;
import com.rejs.registration.domain.lecture.service.LectureService;
import com.rejs.registration.global.response.BaseResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;

    @PostMapping
    public ResponseEntity<BaseResponse<LectrueDto>> createLecture(@RequestBody CreateLectureRequest request){
        LectrueDto lectrueDto = lectureService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.of(HttpStatus.CREATED, lectrueDto));
    }

    @GetMapping("/{id}")
    public BaseResponse<LectrueDto> getById(@PathVariable("id") Long id){
        LectrueDto lectrueDto = lectureService.findById(id);
        return BaseResponse.of(HttpStatus.OK, lectrueDto);
    }


}
