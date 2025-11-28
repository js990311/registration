package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "lectures")
public class Lecture {
    @Id
    @GeneratedValue
    @Column(name = "lecture_id")
    private Long id;

    @Column
    private Integer capacity;

    @Column
    private String name;

    @Column
    private Integer credit;

    @Column
    private Integer studentCount=0;

    // # 관계

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

    // # 생성

    public Lecture(String name, Integer capacity, Integer credit) {
        this.capacity = capacity;
        this.name = name;
        this.credit = credit;
    }

    // # 로직

    public void increaseStudentCount(){
        this.studentCount = Math.min(capacity, this.studentCount+1);
    }

    public void decreaseStudentCount(){
        this.studentCount = Math.max(0, this.studentCount-1);
    }

    public boolean hasCapacity(){
        return this.capacity > this.studentCount;
    }

}
