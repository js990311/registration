package com.rejs.registration.global.authentication;

import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.dto.LoginRequest;
import com.rejs.registration.global.authentication.dto.LoginResponse;
import com.rejs.registration.global.authentication.exception.AuthenticationFailException;
import com.rejs.registration.global.authentication.token.TokenIssuer;
import com.rejs.registration.global.authentication.token.Tokens;
import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final TokenIssuer tokenIssuer;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // 로그인 실패하면 예외를 발생시킴
            log.info("[Login.Success] User : {}", request.getUsername());
            Tokens token = tokenIssuer.issue(authenticate.getName(), authenticate.getAuthorities());
            Student student = studentRepository.findById(Long.parseLong(authenticate.getName())).orElseThrow();
            return new LoginResponse(token, student.getName());
        }catch (BadCredentialsException ex){ // BadCredentialException
            log.warn("[Login.Failed] Invalid Credentials - User : {}", request.getUsername());
            throw AuthenticationFailException.userInfoMismatch();
        }
    }

    public LoginResponse signup(LoginRequest request) {
        Student student = new Student(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        student = studentRepository.save(student);
        log.info("[Signup.Success] User : {}", request.getUsername());
        Tokens tokens = tokenIssuer.issue(student.getId().toString(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return new LoginResponse(tokens, student.getName());
    }


}
