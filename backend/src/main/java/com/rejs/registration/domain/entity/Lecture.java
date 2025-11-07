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

    /*
        반정규화 컬럼이지만 비관적-낙관적 lock을 위해서 추가
     */
    @Column
    private Integer studentCount;

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

    public Lecture(String name, Integer capacity) {
        this.capacity = capacity;
        this.name = name;
        this.studentCount = 0;
    }

    void increaseStudent(){
        this.studentCount++;
    }

    void decreaseStudent(){
        this.studentCount--;
    }

    public boolean hasCapacity(){
        return this.studentCount < this.capacity;
    }
}
