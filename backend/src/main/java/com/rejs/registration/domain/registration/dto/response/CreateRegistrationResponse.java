package com.rejs.registration.domain.registration.dto.response;

import com.rejs.registration.domain.entity.Registration;
import lombok.Getter;

@Getter
public class CreateRegistrationResponse {
    private Long lectureId;
    private Long registrationId;

    public CreateRegistrationResponse(Long lectureId, Long registrationId) {
        this.lectureId = lectureId;
        this.registrationId = registrationId;
    }

    public static CreateRegistrationResponse from(Registration registration){
        return new CreateRegistrationResponse(registration.getLectureId(), registration.getId());
    }
}
