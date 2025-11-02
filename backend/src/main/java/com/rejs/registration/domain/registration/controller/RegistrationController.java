package com.rejs.registration.domain.registration.controller;

import com.rejs.registration.domain.registration.dto.reqeust.CreateRegistrationRequest;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<BaseResponse<CreateRegistrationResponse>> createRegistration(@RequestBody CreateRegistrationRequest request, @RequestHeader("X-Temp-Authentication") Long authentication){
        CreateRegistrationResponse response = registrationService.create(authentication, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.of(HttpStatus.CREATED, response));
    }
}
