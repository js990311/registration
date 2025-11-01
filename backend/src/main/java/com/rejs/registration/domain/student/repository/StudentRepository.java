package com.rejs.registration.domain.student.repository;

import com.rejs.registration.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
