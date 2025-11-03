package com.rejs.registration.global.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.student.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    void clear(){
        studentRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        String username = "username";
        String password = "password";
        Map<String, Object> request = Map.of("username",  username, "password", password);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header.status").value(200))
                .andExpect(jsonPath("$.header.message").value("OK"))
                .andExpect(jsonPath("$.body.accessToken").isString())
                .andExpect(jsonPath("$.body.refreshToken").isString())
        ;
    }

    @Test
    void signup() throws Exception{
        String username = "username";
        String password = "password";
        Map<String, Object> request = Map.of("username",  username, "password", password);

        ResultActions result = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.header.status").value(201))
                .andExpect(jsonPath("$.header.message").value("Created"))
                .andExpect(jsonPath("$.body.accessToken").isString())
                .andExpect(jsonPath("$.body.refreshToken").isString())
        ;
    }

    @Test
    void loginFail() throws Exception {
        String username = "username";
        String password = "password";
        Map<String, Object> request = Map.of("username",  username, "password", password);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        request = Map.of("username",  username, "password", "anotherPassword");
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.header.status").value(401))
                .andExpect(jsonPath("$.header.message").value("UserInfoMismatch"))
                .andExpect(jsonPath("$.body").isEmpty())
        ;
    }

}