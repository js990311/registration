package com.rejs.registration.global.authentication;

import com.rejs.registration.domain.entity.Student;
import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.domain.student.service.StudentService;
import com.rejs.registration.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByName(username).orElseThrow(() -> new GlobalException("AuthenticationFail", HttpStatus.UNAUTHORIZED));
        return new User(student.getId().toString(), student.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}
