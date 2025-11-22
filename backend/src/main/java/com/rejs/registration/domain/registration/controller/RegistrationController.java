package com.rejs.registration.domain.registration.controller;

import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.exception.RegistrationBusinessException;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.global.authentication.claims.annotation.TokenClaim;
import com.rejs.registration.global.response.BaseResponse;
import com.rejs.token_starter.token.ClaimsDto;
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
    public ResponseEntity<CreateRegistrationResponse> createRegistration(@RequestBody CreateRegistrationRequest request, @TokenClaim ClaimsDto claims){
        if(!registrationService.validateRegistarionPeriod(LocalDateTime.now())){
            throw RegistrationBusinessException.notRegistrationPeriod();
        }
        CreateRegistrationResponse response = registrationService.create(claims, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelRegistrationByLectureId(@RequestParam("lectureId") Long id, @TokenClaim ClaimsDto claims){
        if(!registrationService.validateRegistarionPeriod(LocalDateTime.now())){
            throw RegistrationBusinessException.notRegistrationPeriod();
        }
        registrationService.cancel(claims, id);
        return ResponseEntity.noContent().build();
    }
}
