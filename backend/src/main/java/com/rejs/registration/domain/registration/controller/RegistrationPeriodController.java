package com.rejs.registration.domain.registration.controller;

import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.registration.dto.RegistrationPeriodDto;
import com.rejs.registration.domain.registration.dto.reqeust.CreatePeriodRequest;
import com.rejs.registration.domain.registration.service.RegistrationPeriodService;
import com.rejs.registration.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/registrations/periods")
@RequiredArgsConstructor
@Controller
public class RegistrationPeriodController {
    private final RegistrationPeriodService registrationPeriodService;

    @PostMapping
    public ResponseEntity<BaseResponse<RegistrationPeriodDto>> createPeriod(@RequestBody CreatePeriodRequest request){
        RegistrationPeriodDto period = registrationPeriodService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.of(HttpStatus.CREATED,period));
    }
}
