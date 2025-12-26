package com.rejs.registration.domain.registration.controller;

import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.exception.RegistrationBusinessException;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.global.authentication.claims.annotation.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<CreateRegistrationResponse> createRegistration(@RequestBody CreateRegistrationRequest request, @UserId Long userId){
        if(!registrationService.validateRegistarionPeriod(LocalDateTime.now())){
            throw RegistrationBusinessException.notRegistrationPeriod();
        }
        CreateRegistrationResponse response = registrationService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelRegistrationByLectureId(@RequestParam("lectureId") Long id, @UserId Long userId){
        if(!registrationService.validateRegistarionPeriod(LocalDateTime.now())){
            throw RegistrationBusinessException.notRegistrationPeriod();
        }
        registrationService.cancel(userId, id);
        return ResponseEntity.noContent().build();
    }
}
