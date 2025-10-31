package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "registrations")
public class Registration {
    @Id
    @Column(name = "registration_id")
    private Long id;


    // 관계 - student

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    /**
     * entity 패키지 내부에서만 사용
     * @param student 매핑할 student
     */
    void mapStudent(Student student){
        this.student = student;
    }

    /**
     * student와 매핑관계를 할당하는 헬퍼 메서드
     * @param assignStudent 매핑할 student
     */
    public void assignStudent(Student assignStudent){
        if(this.student!=null){
            this.student.removeRegistration(this);
        }
        if(assignStudent != null){
            assignStudent.addRegistration(this);
        }
    }


    // 관계 - lecture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * entity 패키지 내부에서만 사용
     * @param lecture 매핑할 lecture
     */
    void mapLecture(Lecture lecture){
        this.lecture = lecture;
    }

    /**
     * student와 매핑관계를 할당하는 헬퍼 메서드
     * @param assignLecture 매핑할 lecture
     */
    public void assignLecture(Lecture assignLecture){
        if(this.lecture!=null){
            this.lecture.removeRegistration(this);
        }
        if(assignLecture != null){
            assignLecture.addRegistration(this);
        }
    }

}
