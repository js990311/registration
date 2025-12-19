package com.rejs.registration.domain.registration.controller.concurrency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.Registration;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationCancelConcurrencyTest {
    @Autowired
    private TestRestTemplate template;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    private int port;

    private String lectureName = "동시성강의";
    private Integer capacity = 30;
    private Integer credit = 3;
    private Integer studentCount = 80;
    private Integer cancelStudentCount = 20;
    private Long lectureId;
    private List<String> cancelTokens = new ArrayList<>();
    private Integer retry = 10;

    @BeforeEach
    void setup(){
        // 수강신청할 강의 생성
        Lecture lecture = new Lecture(lectureName, capacity,credit);
        lecture = lectureRepository.save(lecture);
        lectureId = lecture.getId();

        // 수강신청 기한 생성
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));

        // 이미 수강이 완료된 학생 생성 -> 수강신청과 취소를 동시에 진행하기 위해서
        for (int i=0;i<cancelStudentCount;i++){
            Student student = new Student("canceled_username" + i, passwordEncoder.encode("password"));
            student = studentRepository.save(student);

            lecture.increaseStudentCount();
            Registration registration = new Registration(student, lecture);
            registrationRepository.save(registration);

            LoginRequest loginRequest = new LoginRequest(student.getName(), "password");
            cancelTokens.add(authenticationService.login(loginRequest).getAccessToken());
        }
        lectureRepository.save(lecture);
    }

    @Test
    void test() throws Exception {
        // 수강신청을 시도할 학생 생성
        List<String> tokens = new ArrayList<>();
        for(int i=0;i<studentCount;i++){
            LoginRequest loginRequest = new LoginRequest("test_username" + i, "password");
            tokens.add(authenticationService.signup(loginRequest).getAccessToken());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(studentCount + cancelStudentCount);

        CountDownLatch readyLatch = new CountDownLatch(studentCount + cancelStudentCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(studentCount + cancelStudentCount);

        AtomicInteger error = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();

        AtomicInteger cancelSuccess = new AtomicInteger();
        AtomicInteger cancelFail = new AtomicInteger();

        // 수강취소 준비
        for (final String token : cancelTokens){
            executorService.submit(() -> {
                try {
                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(MediaType.APPLICATION_JSON);
                    header.setBearerAuth(token);
                    HttpEntity<Void> request = new HttpEntity<>(header);

                    readyLatch.countDown();;
                    readyLatch.await();
                    startLatch.await();

                    String uri = UriComponentsBuilder.fromPath("/registrations")
                            .queryParam("lectureId", lectureId)
                            .build().toUriString();

                    ResponseEntity<Void> response = template.exchange(uri, HttpMethod.DELETE, request, Void.class);

                    if (response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)){
                        cancelSuccess.incrementAndGet();
                    }else {
                        cancelFail.incrementAndGet();
                    }
                }catch (Exception ex){
                    error.incrementAndGet();
                }finally {
                    endLatch.countDown();
                }
            });
        }

        // 수강신청 준비
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

                    for (int r=1;r<=retry;r++){
                        try {
                            ResponseEntity<CreateRegistrationResponse> response = template.postForEntity("/registrations", request, CreateRegistrationResponse.class);
                            if (response.getStatusCode().isSameCodeAs(HttpStatus.CREATED)){
                                success.incrementAndGet();
                                break;
                            }else if(r == retry){
                                if (response.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)){
                                    fail.incrementAndGet();
                                }else {
                                    error.incrementAndGet();
                                }
                            }else {
                                Thread.sleep(100);
                            }
                        }catch (Exception ex){
                            if(r == retry){
                                error.incrementAndGet();
                            }else {
                                Thread.sleep(100);
                            }
                        }
                    }
                }catch (Exception ex){
                    error.incrementAndGet();
                }finally {
                    endLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        endLatch.await();

        assertTrue(capacity >= success.get());
        assertTrue(studentCount - capacity <= fail.get());
        assertEquals(0, error.get());

        assertEquals(cancelStudentCount,cancelSuccess.get());
        assertEquals(0,cancelFail.get());
    }
}
