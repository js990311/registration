package com.rejs.registration.domain.registration.repository;

import com.rejs.registration.domain.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    @Query("select rp from RegistrationPeriod rp where rp.isEmergencyStopped=false and rp.startTime <= :now and :now <= rp.endTime")
    List<RegistrationPeriod> findByPeroid(LocalDateTime now);
}
