package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
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
    }

}