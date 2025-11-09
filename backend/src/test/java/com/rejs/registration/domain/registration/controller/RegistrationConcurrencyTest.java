package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.domain.student.service.StudentService;
import com.rejs.registration.global.authentication.AuthenticationService;
import com.rejs.registration.global.authentication.LoginRequest;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationConcurrencyTest {
    @Autowired
    private TestRestTemplate template;

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
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    private int port;

    private String lectureName = "동시성강의";
    private Integer capacity = 30;
    private Integer studentCount = 100;
    private Long lectureId;

    @BeforeEach
    void setup(){
        // 수강신청할 강의 생성
        Lecture lecture = new Lecture(lectureName, capacity);
        lecture = lectureRepository.save(lecture);
        lectureId = lecture.getId();

        // 수강신청 기한 생성
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));
    }

    @Test
    void test() throws Exception {
        List<String> tokens = new ArrayList<>();
        for(int i=0;i<studentCount;i++){
            LoginRequest loginRequest = new LoginRequest("testusername" + i, "password");
            tokens.add(authenticationService.signup(loginRequest).getAccessToken());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(studentCount);

        CountDownLatch readyLatch = new CountDownLatch(studentCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(studentCount);

        AtomicInteger error = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();


        Map<String, Object> requestBody = Map.of("lectureId", lectureId);


        for (final String token : tokens){
            executorService.submit(() -> {
                try {
                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(MediaType.APPLICATION_JSON);
                    header.setBearerAuth(token);
                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, header);

                    readyLatch.countDown();;
                    readyLatch.await();
                    startLatch.await();
                    ResponseEntity<CreateRegistrationResponse> response = template.postForEntity("/registrations", request, CreateRegistrationResponse.class);
                    if (response.getStatusCode().isSameCodeAs(HttpStatus.CREATED)){
                        success.incrementAndGet();
                    }else if (response.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)){
                        fail.incrementAndGet();
                    }else {
                        error.incrementAndGet();
                    }
                }catch (Exception ex){
                    fail.incrementAndGet();
                }finally {
                    endLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        endLatch.await();
        assertEquals(capacity, success.get());
        assertEquals(studentCount - capacity, fail.get());
        assertEquals(0, error.get());
    }
}
