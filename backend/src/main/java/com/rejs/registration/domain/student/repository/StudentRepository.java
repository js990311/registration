package com.rejs.registration.domain.student.repository;

import com.rejs.registration.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByName(String name);
}
