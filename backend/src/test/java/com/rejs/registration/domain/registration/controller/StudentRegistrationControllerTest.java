package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class StudentRegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RegistrationPeriodRepository periodRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private String student1Token;
    private Long studentId;

    private int lectureSize = 45;
    private int pageNumber = 1;
    private int pageSize = 20;


    @BeforeEach
    void setup(){
        Student student1 = new Student("student1");
        student1 = studentRepository.save(student1);
        studentId = student1.getId();
        student1Token = jwtUtils.generateToken(student1.getId().toString(), "ROLE_USER").getAccessToken();

        for(int i=1;i<=lectureSize;i++){
            Lecture lecture = new Lecture("이름" + i, 30);
            lectureRepository.save(lecture);
            registrationRepository.save(new Registration(student1, lecture));
        }
    }

    @AfterEach
    void clear(){
        registrationRepository.deleteAll();
        studentRepository.deleteAll();
        lectureRepository.deleteAll();
    }

    @Test
    void getStudentRegistration() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/students/me/registrations")
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("page", String.valueOf(pageNumber))
                        .header("Authorization", "Bearer " + student1Token)
        );

        result.andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())

                .andExpect(jsonPath("$.data[0].lectureId").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].capacity").isNumber())
                .andExpect(jsonPath("$.data[0].registrationId").isNumber())


                .andExpect(jsonPath("$.count").value(pageSize))
                .andExpect(jsonPath("$.count").isNumber())

                .andExpect(jsonPath("$.totalElements").value(lectureSize))
                .andExpect(jsonPath("$.totalElements").isNumber())

                .andExpect(jsonPath("$.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.pageNumber").isNumber())

                .andExpect(jsonPath("$.pageSize").value(pageSize))
                .andExpect(jsonPath("$.pageSize").isNumber())

                .andExpect(jsonPath("$.hasNextPage").isBoolean())
                .andExpect(jsonPath("$.hasNextPage").value(true))

        ;
    }


}