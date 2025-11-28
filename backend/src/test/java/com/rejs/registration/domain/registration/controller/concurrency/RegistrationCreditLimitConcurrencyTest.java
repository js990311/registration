package com.rejs.registration.domain.registration.controller.concurrency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Lecture;
import com.rejs.registration.domain.entity.RegistrationPeriod;
import com.rejs.registration.domain.lecture.repository.LectureRepository;
import com.rejs.registration.domain.registration.dto.response.CreateRegistrationResponse;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import com.rejs.registration.domain.registration.repository.RegistrationRepository;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.AuthenticationService;
import com.rejs.registration.global.authentication.LoginRequest;
import com.rejs.token_starter.token.ClaimsDto;
import com.rejs.token_starter.token.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationCreditLimitConcurrencyTest {
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

    private String lectureName = UUID.randomUUID().toString();
    private Integer capacity = 30;
    private Integer credit = 3;
    private Integer lectureCount = 30;
    private List<Long> lectureIds = new ArrayList<>();

    @Test
    void test() throws Exception {
        // g : 30개의 강의. 한명의 학생
        LoginRequest loginRequest = new LoginRequest("user", "password");
        String token = authenticationService.signup(loginRequest).getAccessToken();

        // 수강신청 기한 생성
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.plusDays(7);
        periodRepository.save(new RegistrationPeriod(start, end));

        for(int i=1;i<=lectureCount;i++){
            Lecture lecture = new Lecture(lectureName, capacity, credit);
            lecture = lectureRepository.save(lecture);
            lectureIds.add(lecture.getId());
        }

        // w : 하나의 학생이 매크로 등을 사용하여 수십개의 강의를 동시에 수강신청하는 상황
        CountDownLatch readyLatch = new CountDownLatch(lectureCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(lectureCount);

        AtomicInteger error = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(lectureCount);
        for (Long lectureId: lectureIds){
            Map<String, Object> requestBody = Map.of("lectureId", lectureId);
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
                    error.incrementAndGet();
                }finally {
                    endLatch.countDown();
                }
            });

        }

        readyLatch.await();
        startLatch.countDown();
        endLatch.await();

        ClaimsDto claims = jwtUtils.getClaims(token);
        // t
        assertEquals(6 ,success.get());
        assertEquals(18, registrationRepository.sumCredit(Long.valueOf(claims.getUsername())));
    }
}
