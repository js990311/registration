package com.rejs.registration.domain.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.global.problem.ProblemCode;
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
                .andExpect(jsonPath("$.lectureId").isNumber())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.capacity").value(capacity))
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
                .andExpect(jsonPath("$.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.capacity").value(capacity))
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
                .andExpect(jsonPath("$.type").value(ProblemCode.LECTURE_NOT_FOUND.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.LECTURE_NOT_FOUND.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.LECTURE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/lectures/0"))
        ;

    }

}