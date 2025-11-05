package com.rejs.registration.domain.registration.dto.reqeust;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreatePeriodRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
