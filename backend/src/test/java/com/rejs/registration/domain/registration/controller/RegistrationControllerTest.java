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
import com.rejs.registration.global.authentication.token.TokenIssuer;
import com.rejs.registration.global.problem.ProblemCode;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
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
    private TokenIssuer tokenIssuer;

    private Long lectureId = 1L;
    private Long lecture2Id;
    private String lectureName = "동시성강의";
    private Integer capacity = 2;
    private Integer credit = 3;

    private Long student1Id;
    private Long student2Id;
    private Long student3Id;

    private String student1Token;
    private String student2Token;
    private String student3Token;

    private Long registrationId;

    @BeforeEach
    void setup(){
        Lecture lecture = new Lecture(lectureName, capacity, credit);
        lecture = lectureRepository.save(lecture);
        lectureId = lecture.getId();

        Student student1 = new Student("student1");
        student1 = studentRepository.save(student1);
        student1Id = student1.getId();
        student1Token = tokenIssuer.issue(student1.getId().toString(), List.of(new SimpleGrantedAuthority("ROLE_USER"))).getAccessToken();

        Student student2 = new Student("student2");
        student2 = studentRepository.save(student2);
        student2Id = student2.getId();
        student2Token = tokenIssuer.issue(student2.getId().toString(), List.of(new SimpleGrantedAuthority("ROLE_USER"))).getAccessToken();

        Student student3 = new Student("student3");
        student3 = studentRepository.save(student3);
        student3Id = student3.getId();
        student3Token = tokenIssuer.issue(student3.getId().toString(), List.of(new SimpleGrantedAuthority("ROLE_USER"))).getAccessToken();

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));
        Lecture lecture2 = new Lecture(lectureName, capacity, credit);
        lecture2 = lectureRepository.save(lecture2);
        lecture2Id = lecture2.getId();

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
                .andExpect(jsonPath("$.data.lectureId").value(lectureId))
                .andExpect(jsonPath("$.data.registrationId").isNumber())
        ;

        result.andDo(
                document(builder->builder
                        .requestHeaders(authorizationHeader())
                        .requestFields(
                                fieldWithPath("lectureId").type(JsonFieldType.NUMBER).description("강의 ID")
                        )
                        .responseFields(
                                fieldWithPath("data.lectureId").type(JsonFieldType.NUMBER).description("강의 ID"),
                                fieldWithPath("data.registrationId").type(JsonFieldType.NUMBER).description("수강신청 내역 ID")
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

        andExpectException(result, ProblemCode.INVALID_TOKEN, "/registrations");

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

        andExpectException(result, ProblemCode.LECTURE_NOT_FOUND, "/registrations");

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

        andExpectException(result, ProblemCode.LECTURE_ALREADY_FULL, "/registrations");

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

        andExpectException(result, ProblemCode.ALREADY_REGISTRATION, "/registrations");

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

        andExpectException(result, ProblemCode.NOT_REGISTRATION_PERIOD, "/registrations");

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
    void cancelRegistration() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations").queryParam("lectureId", String.valueOf(lecture2Id))
                .header("Authorization", "Bearer " + student1Token)
        );

        result.andExpect(status().isNoContent());

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("lectureId").description("강의 id")
                        )
                        .requestHeaders(authorizationHeader())
                )
        );
    }

    @Test
    @DisplayName("인증 실패")
    void cancelRegistration401() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations").queryParam("lectureId", String.valueOf(lecture2Id))
                .header("Authorization", "Bearer " + "")
        );

        andExpectException(result, ProblemCode.INVALID_TOKEN, "/registrations");

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("lectureId").description("강의 id")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }

    @Test
    @DisplayName("다른 사람의 수강신청 취소 시도")
    void cancelRegistration403() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations").queryParam("lectureId", String.valueOf(lecture2Id))
                .header("Authorization", "Bearer " + student2Token)
        );

        andExpectException(result, ProblemCode.REGISTRATION_NOT_FOUND, "/registrations");

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("lectureId").description("강의 id")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }

    @Test
    @DisplayName("존재하지 않는 수강신청 내역")
    void cancelRegistration404() throws Exception{
        ResultActions result = mockMvc.perform(delete("/registrations").queryParam("lectureId", String.valueOf(lectureId))
                .header("Authorization", "Bearer " + student1Token)
        );

        andExpectException(result, ProblemCode.REGISTRATION_NOT_FOUND, "/registrations");

        result.andDo(
                document(builder->builder
                        .queryParameters(
                                parameterWithName("lectureId").description("강의 id")
                        )
                        .requestHeaders(authorizationHeader())
                        .responseFields(problemFields())
                )
        );
    }


}