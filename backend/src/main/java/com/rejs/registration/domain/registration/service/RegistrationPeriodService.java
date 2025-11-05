package com.rejs.registration.domain.registration.service;

import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.registration.dto.RegistrationPeriodDto;
import com.rejs.registration.domain.registration.dto.reqeust.CreatePeriodRequest;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegistrationPeriodService {
    private final RegistrationPeriodRepository periodRepository;

    public RegistrationPeriodDto create(CreatePeriodRequest request) {
        RegistrationPeriod registrationPeriod = new RegistrationPeriod(request.getStartTime(), request.getEndTime());
        registrationPeriod = periodRepository.save(registrationPeriod);
        return RegistrationPeriodDto.from(registrationPeriod);
    }
}
