package com.rejs.registration.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.authentication.UserDetailServiceImpl;
import com.rejs.registration.global.authentication.exception.AuthenticationFailException;
import com.rejs.registration.global.exception.GlobalException;
import com.rejs.registration.global.response.BaseResponse;
import com.rejs.token_starter.config.AutoJwtTokenConfiguration;
import com.rejs.token_starter.config.JwtProperties;
import com.rejs.token_starter.filter.JwtAuthenticationFilter;
import com.rejs.token_starter.token.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(((request, response, ex) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            PrintWriter writer = response.getWriter();
                            writer.write(mapper.writeValueAsString(BaseResponse.fromException(AuthenticationFailException.invalidToken())));
                        }))
                        .accessDeniedHandler((requst, response, ex)->{
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            PrintWriter writer = response.getWriter();
                            writer.write(mapper.writeValueAsString(BaseResponse.fromException(new GlobalException("AccessDenied", HttpStatus.FORBIDDEN))));
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
