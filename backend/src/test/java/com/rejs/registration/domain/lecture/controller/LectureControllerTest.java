package com.rejs.registration.domain.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
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
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LectureRepository lectureRepository;

    String name = "동시성";
    Integer capacity = 30;

    @Test
    void createLecture() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name,
                "capacity", capacity
        );

        ResultActions result = mockMvc.perform(
                post("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.header.status").value(201))
                .andExpect(jsonPath("$.header.message").value("Created"))
                .andExpect(jsonPath("$.body.lectureId").isNumber())
                .andExpect(jsonPath("$.body.name").value(name))
                .andExpect(jsonPath("$.body.capacity").value(capacity))
        ;
    }

    @Test
    void readLectureById() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name,
                "capacity", capacity
        );

        Lecture lecture = lectureRepository.save(new Lecture(name, capacity));


        ResultActions result = mockMvc.perform(
                get("/lectures/{id}", lecture.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.header.status").value(200))
                .andExpect(jsonPath("$.header.message").value("OK"))
                .andExpect(jsonPath("$.body.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.body.name").value(name))
                .andExpect(jsonPath("$.body.capacity").value(capacity))
        ;

    }

    @Test
    void readLectureById404() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/lectures/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.header.status").value(404))
                .andExpect(jsonPath("$.header.message").value("Lecture Not Found"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;

    }

}