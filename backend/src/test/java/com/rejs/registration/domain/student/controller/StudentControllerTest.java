package com.rejs.registration.domain.student.controller;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentControllerTest extends AbstractControllerTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private String studentToken;
    private Student student1;

    String name = "동시성";
    @BeforeEach
    void setUp() throws Exception {
        student1 = new Student("student1");
        student1 = studentRepository.save(student1);
        studentToken = jwtUtils.generateToken(student1.getId().toString(), "ROLE_USER").getAccessToken();

    }

    @Test
    void readStudentMe() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/students/me")
                        .header("Authorization","Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentId").value(student1.getId()))
                .andExpect(jsonPath("$.data.name").value(student1.getName()))
                .andExpect(jsonPath("$.data.creditLimit").value(student1.getCreditLimit()))
        ;

        result.andDo(
                document((builder)->builder
                        .responseFields(
                                data(
                                        new FieldDescriptors(
                                                fieldWithPath("studentId").description("학생의 고유번호").type(JsonFieldType.NUMBER),
                                                fieldWithPath("name").description("등록된 학생의 이름").type(JsonFieldType.STRING),
                                                fieldWithPath("creditLimit").description("수강가능학점").type(JsonFieldType.NUMBER)
                                        )
                                )
                        )
                )
        );

    }
}