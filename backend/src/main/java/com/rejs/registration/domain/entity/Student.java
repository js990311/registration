package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue
    @Column(name = "student_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private Integer creditLimit;

    /* 관계 */

    @OneToMany(mappedBy = "student")
    private List<Registration> registrations = new ArrayList<>();

    public void removeRegistration(Registration registration) {
        if(registration != null){
            this.registrations.remove(registration);
            registration.mapStudent(null);
        }
    }

    public void addRegistration(Registration registration) {
        if(registration!=null){
            this.registrations.add(registration);
            registration.mapStudent(this);
        }
    }

    // # 로직

    // # 생성

    public Student(String name) {
        this.name = name;
        this.creditLimit = 18;
    }

    public Student(String name, String password) {
        this.name = name;
        this.password = password;
        this.creditLimit = 18;
    }
}
