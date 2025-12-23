package com.rejs.registration.domain.registration.dto.reqeust;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class CreatePeriodRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
