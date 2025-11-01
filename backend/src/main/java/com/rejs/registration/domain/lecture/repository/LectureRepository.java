package com.rejs.registration.domain.lecture.repository;

import com.rejs.registration.domain.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
