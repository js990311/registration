package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "registration_periods")
public class RegistrationPeriod {
    @Id
    @GeneratedValue
    @Column(name = "registration_period_id")
    private Long id;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Boolean isEmergencyStopped = false;

    public RegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void emergencyStop(){
        this.isEmergencyStopped = true;
    }

    public void cancelEmergencyStop(){
        this.isEmergencyStopped = false;
    }

    public boolean isRegistrationPossible(LocalDateTime now ){
        return !isEmergencyStopped && startTime.isBefore(now) && endTime.isAfter(now);
    }
}
