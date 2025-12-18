package com.rejs.registration.domain.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.AbstractControllerTest;
import com.rejs.registration.TestcontainersConfiguration;
import com.rejs.registration.domain.registration.repository.RegistrationPeriodRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistrationPeriodControllerTest extends AbstractControllerTest {
    @Autowired
    private RegistrationPeriodRepository periodRepository;

    @Test
    void createPeriod() throws Exception{
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Map<String, Object> request = Map.of(
                "startTime", now,
                "endTime", now.plusDays(7)
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        ResultActions result = mockMvc.perform(
                post("/registrations/periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.periodId").isNumber())
                .andExpect(jsonPath("$.data.startTime").value(now.format(formatter)))
                .andExpect(jsonPath("$.data.endTime").value(now.plusDays(7).format(formatter)))
        ;

        result.andDo(
                document((build) ->
                        build
                                .requestFields(
                                        fieldWithPath("startTime").description("수강신청기간의 시작시간").type(JsonFieldType.STRING),
                                        fieldWithPath("endTime").description("수강신청기간의 종료시간").type(JsonFieldType.STRING)
                                )
                                .responseFields(
                                        fieldWithPath("data.periodId").description("수강신청기한의 고유제어번호").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.startTime").description("수강신청기간의 시작시간").type(JsonFieldType.STRING),
                                        fieldWithPath("data.endTime").description("수강신청기간의 종료시간").type(JsonFieldType.STRING)
                                )
                )
        );
    }
}