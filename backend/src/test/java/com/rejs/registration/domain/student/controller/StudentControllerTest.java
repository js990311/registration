package com.rejs.registration.domain.student.controller;

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

    String name = "동시성";
    String token = "Bearer token";

    @BeforeEach
    void setUp() throws Exception {
        Student student1 = new Student("student1");
        student1 = studentRepository.save(student1);
        studentToken = jwtUtils.generateToken(student1.getId().toString(), "ROLE_USER").getAccessToken();

    }

    @Test
    void readStudentById() throws Exception{
        Map<String, Object> request = Map.of(
                "name", name
        );

        Student student = studentRepository.save(new Student(name));


        ResultActions result = mockMvc.perform(
                get("/students/{id}", student.getId())
                        .header("Authorization","Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.name").value(name))
        ;

        result.andDo(
                document((builder)->builder
                        .pathParameters(
                                parameterWithName("id").description("조회할 학생의 id")
                        )
                        .responseFields(
                                fieldWithPath("studentId").description("학생의 고유번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("등록된 학생의 이름").type(JsonFieldType.STRING)
                        )
                )
        );

    }

    @Test
    void readLectureById404() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/students/{id}", 0)
                        .header("Authorization","Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value(ProblemCode.STUDENT_NOT_FOUND.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.STUDENT_NOT_FOUND.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.STUDENT_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/students/0"))
        ;

        result.andDo(document(
                (builder)->
                        builder
                                .requestHeaders(authorizationHeader())
                                .responseSchema(problemSchema())
                                .responseFields(problemFields())
        ));
    }

}