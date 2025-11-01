package com.rejs.registration.global.exception;

import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.Student;
import org.springframework.http.HttpStatus;

public class NotFoundException extends GlobalException{
    public NotFoundException(String message, HttpStatus status) {
        super(message, status);
    }

    public static NotFoundException of(Class<?> clazz){
        return new NotFoundException(clazz.getSimpleName() + " Not Found", HttpStatus.NOT_FOUND);
    }

    public static NotFoundException lectureNotFound(){
        return NotFoundException.of(Lecture.class);
    }

    public static NotFoundException studentNotFound(){
        return NotFoundException.of(Student.class);
    }

    public static NotFoundException registrationNotFound(){
        return NotFoundException.of(Registration.class);
    }

}
