package com.rejs.registration.domain.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    String name = "동시성";

    @Test
    void createStudent() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name
        );

        ResultActions result = mockMvc.perform(
                post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.header.status").value(201))
                .andExpect(jsonPath("$.header.message").value("Created"))
                .andExpect(jsonPath("$.body.studentId").isNumber())
                .andExpect(jsonPath("$.body.name").value(name))
        ;
    }

    @Test
    void readStudentById() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name
        );

        Student student = studentRepository.save(new Student(name));


        ResultActions result = mockMvc.perform(
                get("/students/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.header.status").value(200))
                .andExpect(jsonPath("$.header.message").value("OK"))
                .andExpect(jsonPath("$.body.studentId").value(student.getId()))
                .andExpect(jsonPath("$.body.name").value(name))
        ;

    }

    @Test
    void readLectureById404() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/students/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.header.status").value(404))
                .andExpect(jsonPath("$.header.message").value("Student Not Found"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;

    }

}