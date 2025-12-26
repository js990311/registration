package com.rejs.registration.domain.registration.controller;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.token.TokenIssuer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentRegistrationControllerTest extends AbstractControllerTest {
    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RegistrationPeriodRepository periodRepository;

    @Autowired
    private TokenIssuer tokenIssuer;

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
        student1Token = tokenIssuer.issue(student1.getId().toString(), List.of(new SimpleGrantedAuthority("ROLE_USER"))).getAccessToken();

        for(int i=1;i<=lectureSize;i++){
            Lecture lecture = new Lecture("이름" + i, 30, 3);
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
                .andExpect(jsonPath("$.data[0].credit").isNumber())

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
        ;

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        )
                        .responseFields(
                                paginationFields().andWithPrefix(
                                        "data[].", mergeFields(new FieldDescriptors(
                                                fieldWithPath("lectureId")
                                                        .description("강의 id ")
                                                        .type(JsonFieldType.NUMBER),
                                                fieldWithPath("name")
                                                        .description("강의명")
                                                        .type(JsonFieldType.STRING),
                                                fieldWithPath("capacity")
                                                        .description("강의 수강신청가능인원")
                                                        .type(JsonFieldType.NUMBER),
                                                fieldWithPath("registrationId")
                                                        .description("수강등록 id")
                                                        .type(JsonFieldType.NUMBER),
                                                fieldWithPath("credit")
                                                        .description("학점")
                                                        .type(JsonFieldType.NUMBER)
                                        ))
                                )
                        ))
        );

    }


}