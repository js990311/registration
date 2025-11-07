package com.rejs.registration.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "registrations", uniqueConstraints = {
        @UniqueConstraint(name = "duplicate_registraion", columnNames = {"lecture_id", "student_id"})
})
public class Registration {
    @Id
    @GeneratedValue
    @Column(name = "registration_id")
    private Long id;


    // 관계 - student

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public Long getStudentId(){
        return student != null ? student.getId() : null;
    }

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

    public Long getLectureId(){
        return lecture != null ? lecture.getId() : null;
    }

    public Registration(Student student, Lecture lecture) {
        this.student = student;
        this.lecture = lecture;
        this.lecture.increaseStudent();
    }
}
