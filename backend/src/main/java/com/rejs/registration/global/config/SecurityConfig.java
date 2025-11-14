package com.rejs.registration.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.UserDetailServiceImpl;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.registration.global.response.ProblemResponse;
import com.rejs.token_starter.config.JwtProperties;
import com.rejs.token_starter.filter.JwtAuthenticationFilter;
import com.rejs.token_starter.token.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({JwtProperties.class})
public class SecurityConfig {
    private final ObjectMapper mapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtUtils jwtUtils(JwtProperties properties) {
        if (!StringUtils.hasText(properties.getSecretKey())) {
            throw new IllegalArgumentException("Require jwt.secret-key in Properties");
        } else {
            return new JwtUtils(properties.getSecretKey(), properties.getAccessTokenExpiration(), properties.getRefreshTokenExpiration());
        }
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/login", "/signup", "/public/**", "/lectures/**").permitAll()
                        .requestMatchers("/registrations/periods").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(((request, response, ex) -> {
                            response.setContentType("application/json");
                            ProblemCode code = ProblemCode.INVALID_TOKEN;
                            response.setStatus(code.getStatus().value());
                            PrintWriter writer = response.getWriter();
                            writer.write(mapper.writeValueAsString(new ProblemResponse(code, request.getRequestURI(), null)));
                        }))
                        .accessDeniedHandler((request, response, ex)->{
                            response.setContentType("application/json");
                            ProblemCode code = ProblemCode.ACCESS_DENIED;
                            response.setStatus(code.getStatus().value());
                            PrintWriter writer = response.getWriter();
                            writer.write(mapper.writeValueAsString(new ProblemResponse(code, request.getRequestURI(), null)));
                        }
                ))
        ;
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(StudentRepository studentRepository){
        return new UserDetailServiceImpl(studentRepository);
    }
}
