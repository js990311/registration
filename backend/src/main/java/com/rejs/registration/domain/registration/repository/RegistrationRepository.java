package com.rejs.registration.domain.registration.repository;

import com.rejs.registration.domain.entity.Registration;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT count(r)>0 from Registration r where r.lecture.id = :lectureId and r.student.id = :studentId")
    boolean isAlreadyRegistered(@Param("lectureId") Long lectureId, @Param("studentId") Long studentId);

    @Query("SELECT count(r) from Registration r where r.lecture.id = :lectureId")
    Long countByLectureId(@Param("lectureId") Long lectureId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r from Registration r where r.id = :id")
    Optional<Registration> findByIdWithLock(@Param("id") Long id);
}
