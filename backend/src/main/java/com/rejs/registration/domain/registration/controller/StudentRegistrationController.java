package com.rejs.registration.domain.registration.controller;

import com.rejs.registration.domain.registration.dto.response.RegistrationLectureDto;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.global.authentication.claims.annotation.TokenClaim;
import com.rejs.token_starter.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/students/me/registrations")
@RestController
public class StudentRegistrationController {
    private final RegistrationService registrationService;


    @GetMapping
    public Page<RegistrationLectureDto> getRegistrationLectures(
            @PageableDefault Pageable pageable,
            @TokenClaim ClaimsDto claims
            ){
          return registrationService.findByStudentId(claims, pageable);
    }
}
