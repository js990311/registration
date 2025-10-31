package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "student_id")
    private Long id;

    @Column
    private String name;

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

}
