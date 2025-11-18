package com.rejs.registration.global.authentication;

import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.exception.AuthenticationFailException;
import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.token_starter.token.ClaimsDto;
import com.rejs.token_starter.token.JwtUtils;
import com.rejs.token_starter.token.Tokens;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    public Tokens login(LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // 로그인 실패하면 예외를 발생시킴
            return jwtUtils.generateToken(authenticate.getName(), "ROLE_USER");
        }catch (BadCredentialsException ex){ // BadCredentialException
            throw AuthenticationFailException.userInfoMismatch();
        }catch (RuntimeException ex){
            throw ex;
        }
    }

    public Tokens signup(LoginRequest request) {
        Student student = new Student(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        student = studentRepository.save(student);
        return jwtUtils.generateToken(student.getId().toString(), "ROLE_USER");
    }

    public Tokens refresh(ClaimsDto claim){
        if(claim!= null && claim.getType().equals("REFRESH")){
            try {
                return jwtUtils.generateToken(claim.getUsername(), "ROLE_USER");
            }catch (BadCredentialsException ex){ // BadCredentialException
                throw AuthenticationFailException.userInfoMismatch();
            }catch (RuntimeException ex){
                throw ex;
            }
        }else {
            throw new BusinessException(ProblemCode.REFRESH_TOKEN_REQUIRED);
        }
    }
}
