package com.rejs.registration.domain.registration.dto;

import com.rejs.registration.domain.entity.RegistrationPeriod;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegistrationPeriodDto {
    private Long periodId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public RegistrationPeriodDto(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        this.periodId = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static RegistrationPeriodDto from(RegistrationPeriod registrationPeriod) {
        return new RegistrationPeriodDto(registrationPeriod.getId(), registrationPeriod.getStartTime(), registrationPeriod.getEndTime());
    }
}
