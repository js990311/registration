package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.registration.service.RegistrationService;
import com.rejs.registration.domain.student.dto.request.CreateStudentRequest;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistrationControllerTest extends AbstractControllerTest {
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

    private Long lectureId = 1L;
    private String lectureName = "동시성강의";
    private Integer capacity = 2;
    private Long student1Id;
    private Long student2Id;
    private Long student3Id;

    private String student1Token;
    private String student2Token;
    private String student3Token;

    private Long registrationId;

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

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));
        Lecture lecture2 = new Lecture(lectureName, capacity);
        lecture2 = lectureRepository.save(lecture2);

        Registration registration = new Registration(student1, lecture2);
        registrationId = registrationRepository.save(registration).getId();

    }

    @AfterEach
    void clear(){
        registrationRepository.deleteAll();
        lectureRepository.deleteAll();
        studentRepository.deleteAll();
        periodRepository.deleteAll();;
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
                .andExpect(jsonPath("$.lectureId").value(lectureId))
                .andExpect(jsonPath("$.registrationId").isNumber())
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID"),
                                fieldWithPath("registrationId").type(JsonFieldType.NUMBER).description("수강신청 내역 ID")
                        )
                )
        );

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
                .andExpect(jsonPath("$.type").value(ProblemCode.INVALID_TOKEN.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.INVALID_TOKEN.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.INVALID_TOKEN.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations"))
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(problemFields())
                )
        );

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
                .andExpect(jsonPath("$.type").value(ProblemCode.LECTURE_NOT_FOUND.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.LECTURE_NOT_FOUND.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.LECTURE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations"))
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(problemFields())
                )
        );

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
                .andExpect(jsonPath("$.type").value(ProblemCode.LECTURE_ALREADY_FULL.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.LECTURE_ALREADY_FULL.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.LECTURE_ALREADY_FULL.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations"))
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(problemFields())
                )
        );

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
                .andExpect(jsonPath("$.type").value(ProblemCode.ALREADY_REGISTRATION.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.ALREADY_REGISTRATION.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.ALREADY_REGISTRATION.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations"))
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(problemFields())
                )
        );

    }

    @Test
    void registFailBecauseisNotRegistrationPeriod() throws Exception{
        // 유효한 수강신청가능시간을 제거하고, 유효하지 않은 수강신청기간 생성
        periodRepository.deleteAll();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));

        Map<String, Object> request = Map.of("lectureId", lectureId);

        ResultActions result = mockMvc.perform(post("/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + student1Token)
        );

        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value(ProblemCode.NOT_REGISTRATION_PERIOD.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.NOT_REGISTRATION_PERIOD.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.NOT_REGISTRATION_PERIOD.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations"))
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(problemFields())
                )
        );

    }

    @Test
    @DisplayName("수강신청 취소")
    void deleteRegistration() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations/{id}", registrationId)
                .header("Authorization", "Bearer " + student1Token)
        );
        result.andExpect(status().isNoContent());

        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("수강신청 ID")
                        )
                        .requestHeaders(authorizationHeader())
                )
        );
    }

    @Test
    @DisplayName("인증 실패")
    void deleteRegistration401() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations/{id}", registrationId)
                .header("Authorization", "Bearer " + "")
        );
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value(ProblemCode.INVALID_TOKEN.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.INVALID_TOKEN.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.INVALID_TOKEN.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations/" + registrationId))
        ;


        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("수강신청 ID")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }

    @Test
    @DisplayName("다른 사람의 수강신청 취소 시도")
    void deleteRegistration403() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations/{id}", registrationId)
                .header("Authorization", "Bearer " + student2Token)
        );
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value(ProblemCode.ACCESS_DENIED.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.ACCESS_DENIED.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.ACCESS_DENIED.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations/" + registrationId))
        ;


        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("수강신청 ID")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }

    @Test
    @DisplayName("존재하지 않는 수강신청 내역")
    void deleteRegistration404() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations/{id}", 0)
                .header("Authorization", "Bearer " + student1Token)
        );
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value(ProblemCode.REGISTRATION_NOT_FOUND.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.REGISTRATION_NOT_FOUND.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.REGISTRATION_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/registrations/0"))
        ;


        result.andDo(
                document(builder->builder
                        .pathParameters(
                                parameterWithName("id").description("수강신청 ID")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }


}