package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "lectures")
public class Lecture {
    @Id
    @Column(name = "lecture_id")
    private Long id;

    @Column
    private Integer capacity;

    @Column
    private String name;

    @OneToMany(mappedBy = "lecture")
    private List<Registration> registrations = new ArrayList<>();

    public void removeRegistration(Registration registration) {
        if(registration != null){
            this.registrations.remove(registration);
            registration.mapLecture(null);
        }
    }

    public void addRegistration(Registration registration) {
        if(registration!=null){
            this.registrations.add(registration);
            registration.mapLecture(this);
        }
    }
}
