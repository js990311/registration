package com.rejs.registration.domain.lecture.controller;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.global.problem.ProblemCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LectureControllerTest extends AbstractControllerTest {

    @Autowired
    private LectureRepository lectureRepository;

    String name = "동시성";
    Integer capacity = 30;
    Integer credit = 3;

    @AfterEach
    void clear(){
        lectureRepository.deleteAll();
    }

    @Test
    void createLecture() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name,
                "capacity", capacity,
                "credit", credit
        );

        ResultActions result = mockMvc.perform(
                post("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.lectureId").isNumber())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.capacity").value(capacity))
                .andExpect(jsonPath("$.data.credit").value(credit))
        ;

        result.andDo(
                document(builder->builder
                        .requestFields(
                                fieldWithPath("name").description("강의이름").type(JsonFieldType.STRING),
                                fieldWithPath("capacity").description("강의 최대 수강인원").type(JsonFieldType.NUMBER),
                                fieldWithPath("credit").description("학점").type(JsonFieldType.NUMBER)
                        )
                        .responseFields(data(lectureFields()))
                )
        );
    }

    @Test
    void readLectureById() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name,
                "capacity", capacity,
                "credit", credit
        );

        Lecture lecture = lectureRepository.save(new Lecture(name, capacity, credit));


        ResultActions result = mockMvc.perform(
                get("/lectures/{id}", lecture.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lectureId").isNumber())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.capacity").value(capacity))
                .andExpect(jsonPath("$.data.credit").value(credit))
        ;

        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("강의 번호")
                        )
                        .responseFields(data(lectureFields()))
                )
        );

    }

    @Test
    void readLectureById404() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/lectures/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        ProblemCode code = ProblemCode.LECTURE_NOT_FOUND;

        andExpectException(result, code, "/lectures/0");

        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("강의 번호")
                        )
                        .responseFields(problemFields())
            )
        );

    }

        @Test
    void readLecturePages() throws Exception{
        int lectureSize = 45;
        for(int i=1;i<=lectureSize;i++){
            Lecture lecture = new Lecture("이름" + i, 30, 3);
            lectureRepository.save(lecture);
        }

        int pageNumber = 1;
        int pageSize = 20;

        ResultActions result = mockMvc.perform(get("/lectures")
                .queryParam("size", String.valueOf(pageSize))
                .queryParam("page", String.valueOf(pageNumber))
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].lectureId").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].capacity").isNumber())
                .andExpect(jsonPath("$.pagination.count").value(pageSize))
                .andExpect(jsonPath("$.pagination.count").isNumber())
                .andExpect(jsonPath("$.pagination.totalElements").value(lectureSize))
                .andExpect(jsonPath("$.pagination.totalElements").isNumber())
                .andExpect(jsonPath("$.pagination.requestNumber").value(pageNumber))
                .andExpect(jsonPath("$.pagination.requestNumber").isNumber())
                .andExpect(jsonPath("$.pagination.requestSize").value(pageSize))
                .andExpect(jsonPath("$.pagination.requestSize").isNumber())
                .andExpect(jsonPath("$.pagination.hasNextPage").isBoolean())
                .andExpect(jsonPath("$.pagination.hasNextPage").value(true))
                .andExpect(jsonPath("$.pagination.totalPage").isNumber())
                .andExpect(jsonPath("$.pagination.blockLeft").isNumber())
                .andExpect(jsonPath("$.pagination.blockRight").isNumber());

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        )
                        .responseFields(
                                paginationFields().andWithPrefix(
                                    "data[].", mergeFields(lectureFields())
                                )
                ))
        );
    }

    // 문서화
    public FieldDescriptors lectureFields(){
        return new FieldDescriptors(
            fieldWithPath("lectureId").description("강의 ID").type(JsonFieldType.NUMBER),
            fieldWithPath("name").description("강의 이름").type(JsonFieldType.STRING),
            fieldWithPath("capacity").description("수강 인원 정원").type(JsonFieldType.NUMBER),
            fieldWithPath("studentCount").description("현재 수강 인원").type(JsonFieldType.NUMBER),
            fieldWithPath("credit").description("학점").type(JsonFieldType.NUMBER)
        );
    }

}