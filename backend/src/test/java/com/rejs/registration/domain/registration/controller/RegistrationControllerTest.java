package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.domain.student.dto.request.CreateStudentRequest;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class RegistrationControllerTest {
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
    private JwtUtils jwtUtils;

    private Long lectureId = 1L;
    private String lectureName = "동시성강의";
    private Integer capacity = 2;
    private Long student1Id;
    private Long student2Id;
    private Long student3Id;

    private String student1Token;
    private String student2Token;
    private String student3Token;

    @BeforeEach
    void setup(){
        Lecture lecture = new Lecture(lectureName, capacity);
        lecture = lectureRepository.save(lecture);
        lectureId = lecture.getId();

        Student student1 = new Student("student1");
        student1 = studentRepository.save(student1);
        student1Id = student1.getId();
        student1Token = jwtUtils.generateToken(student1.getId().toString(), "ROLE_USER").getAccessToken();

        Student student2 = new Student("student2");
        student2 = studentRepository.save(student2);
        student2Id = student2.getId();
        student2Token = jwtUtils.generateToken(student2.getId().toString(), "ROLE_USER").getAccessToken();

        Student student3 = new Student("student3");
        student3 = studentRepository.save(student3);
        student3Id = student3.getId();
        student3Token = jwtUtils.generateToken(student3.getId().toString(), "ROLE_USER").getAccessToken();
    }

    @AfterEach
    void clear(){
        registrationRepository.deleteAll();
        lectureRepository.deleteById(lectureId);
        studentRepository.deleteById(student1Id);
        studentRepository.deleteById(student2Id);
        studentRepository.deleteById(student3Id);
    }

    @Test
    void regist() throws Exception{
        Map<String, Object> request = Map.of("lectureId", lectureId);
        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student1Token)
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.header.status").value(201))
                .andExpect(jsonPath("$.header.message").value("Created"))
                .andExpect(jsonPath("$.body.lectureId").value(lectureId))
                .andExpect(jsonPath("$.body.registrationId").isNumber())
        ;
    }

    @Test
    void registFail401() throws Exception{
        Map<String, Object> request = Map.of("lectureId", lectureId);
        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Not Token")
        );

        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.header.status").value(401))
                .andExpect(jsonPath("$.header.message").value("InvalidToken"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @Test
    void registFail404() throws Exception{
        Map<String, Integer> request = Map.of("lectureId", 0);
        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student1Token)
        );

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.header.status").value(404))
                .andExpect(jsonPath("$.header.message").value("Lecture Not Found"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @Test
    void registFailFullCapacity() throws Exception{
        Map<String, Object> request = Map.of("lectureId", lectureId);

        mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student1Token)
                );
        mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student2Token)
        );
        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student3Token)
        );

        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.header.status").value(409))
                .andExpect(jsonPath("$.header.message").value("Lecture is already full"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

    @Test
    void registFailAlreadyRegist() throws Exception{
        Map<String, Object> request = Map.of("lectureId", lectureId);

        mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student2Token)
        );
        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student2Token)
        );

        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.header.status").value(409))
                .andExpect(jsonPath("$.header.message").value("Lecture already registered"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

}