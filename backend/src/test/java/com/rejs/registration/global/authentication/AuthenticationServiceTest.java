package com.rejs.registration.global.authentication;

import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.dto.LoginRequest;
import com.rejs.registration.global.authentication.dto.LoginResponse;
import com.rejs.registration.global.authentication.exception.AuthenticationFailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String username = "username";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        Student student = new Student(username, passwordEncoder.encode(password));
        studentRepository.save(student);
    }

    @AfterEach
    void cleanUp(){
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공 시 토큰과 학생 정보를 반환한다")
    void signup_success() {
        // given
        String newName = "username2";
        String newPassword = "password2";
        LoginRequest signupRequest = new LoginRequest(newName, newPassword);

        // when
        LoginResponse response = authenticationService.signup(signupRequest);

        // then
        assertAll("회원가입 응답 검증",
                () -> assertNotNull(response.getTokens().getAccessToken()),
                () -> assertEquals(newName, response.getUsername()),
                () -> assertTrue(studentRepository.findByName(newName).isPresent())
        );
    }

    @Test
    @DisplayName("로그인 성공 시 토큰과 StudentDto를 반환한다")
    void login_success() {
        // given
        LoginRequest loginRequest = new LoginRequest(username, password);

        // when
        LoginResponse response = authenticationService.login(loginRequest);

        // then
        assertAll("로그인 응답 검증",
                () -> assertNotNull(response.getTokens().getAccessToken()),
                () -> assertEquals(username, response.getUsername())
        );
    }

    @Test
    @DisplayName("비밀번호가 틀리면 AuthenticationFailException이 발생한다")
    void login_fail_password() {
        // given
        LoginRequest wrongRequest = new LoginRequest(username, "failPassword");

        // when & then
        assertThrows(AuthenticationFailException.class, () -> {
            authenticationService.login(wrongRequest);
        });
    }}