package com.rejs.registration.global.authentication;

import com.epages.restdocs.apispec.*;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.domain.student.repository.StudentRepository;
import com.rejs.registration.global.problem.ProblemCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends AbstractControllerTest {
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
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
        ;

        result
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "/{class-name}/{method-name}",
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .requestFields(
                                                        fieldWithPath("username")
                                                                .description("id")
                                                                .type(JsonFieldType.STRING),
                                                        fieldWithPath("password")
                                                                .description("비밀번호")
                                                                .type(JsonFieldType.STRING)
                                                )
                                                .responseFields(
                                                        tokensFields()
                                                )
                                                .responseSchema(tokensSchema())
                                                .build()
                                )
                        )
                )
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
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
        ;

        result
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "/{class-name}/{method-name}",
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .requestFields(
                                                        fieldWithPath("username")
                                                                .description("id")
                                                                .type(JsonFieldType.STRING),
                                                        fieldWithPath("password")
                                                                .description("비밀번호")
                                                                .type(JsonFieldType.STRING)
                                                )
                                                .responseFields(
                                                        tokensFields()
                                                )
                                                .responseSchema(tokensSchema())
                                                .build()
                                )
                        )
                )
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
                .andExpect(jsonPath("$.type").value(ProblemCode.USER_INFO_MISMATCH.getType()))
                .andExpect(jsonPath("$.title").value(ProblemCode.USER_INFO_MISMATCH.getTitle()))
                .andExpect(jsonPath("$.status").value(ProblemCode.USER_INFO_MISMATCH.getStatus().value()))
                .andExpect(jsonPath("$.instance").value("/login"))
        ;

        result
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "/{class-name}/{method-name}",
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .requestFields(
                                                        fieldWithPath("username")
                                                                .description("id")
                                                                .type(JsonFieldType.STRING),
                                                        fieldWithPath("password")
                                                                .description("비밀번호")
                                                                .type(JsonFieldType.STRING)
                                                )
                                                .responseFields(
                                                        problemFields()
                                                )
                                                .responseSchema(problemSchema())
                                                .build()
                                )
                        )
                )
        ;
    }

    // 문서화
    public FieldDescriptors tokensFields(){
        return new FieldDescriptors(
                fieldWithPath("accessToken")
                        .description("액세스토큰")
                        .type(JsonFieldType.STRING),
                fieldWithPath("refreshToken")
                        .description("리프레스토큰")
                        .type(JsonFieldType.STRING)
        );
    }

    public Schema tokensSchema(){
        return new Schema("tokens");
    }

}