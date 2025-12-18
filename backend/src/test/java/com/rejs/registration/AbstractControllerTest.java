package com.rejs.registration;

import com.epages.restdocs.apispec.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.global.problem.ProblemCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractControllerTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc(final WebApplicationContext context, final RestDocumentationContextProvider provider){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    // 검증 함수
    public void andExpectException(ResultActions result, ProblemCode code, String instance) throws Exception {
        result
                .andExpect(status().is(code.getStatus().value()))
                .andExpect(jsonPath("$.error.type").isString())
                .andExpect(jsonPath("$.error.type").value(code.getType()))
                .andExpect(jsonPath("$.error.title").isString())
                .andExpect(jsonPath("$.error.title").value(code.getTitle()))
                .andExpect(jsonPath("$.error.status").isNumber())
                .andExpect(jsonPath("$.error.status").value(code.getStatus().value()))
                .andExpect(jsonPath("$.error.instance").isString())
                .andExpect(jsonPath("$.error.instance").value(instance))
                .andExpect(jsonPath("$.error.detail").isString())

        ;
    }


    // 문서화 utils 함수

    public RestDocumentationResultHandler document(Function<ResourceSnippetParametersBuilder, ResourceSnippetParametersBuilder> builderFunction){
        return document("/{class-name}/{method-name}", builderFunction);
    }

    public RestDocumentationResultHandler document(String identifier, Function<ResourceSnippetParametersBuilder, ResourceSnippetParametersBuilder> builderFunction){
        return MockMvcRestDocumentationWrapper.document(
                identifier,
                ResourceDocumentation.resource(
                        builderFunction.apply(ResourceSnippetParameters.builder())
                                .build()
                )
        );
    }

    public FieldDescriptors data(FieldDescriptors fields){
        return new FieldDescriptors().andWithPrefix("data.", mergeFields(fields));
    }

    public FieldDescriptor[] mergeFields(FieldDescriptors fields){
        return fields.getFieldDescriptors().toArray(new FieldDescriptor[0]);
    }

    public FieldDescriptors paginationFields(){
        return new FieldDescriptors(
                fieldWithPath("data")
                        .description("불러온 데이터")
                        .type(JsonFieldType.ARRAY),
                fieldWithPath("pagination.count")
                        .description("페이지에 포함된 데이터")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.totalElements")
                        .description("총 개수")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.totalPage")
                        .description("총 페이지 수")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.requestNumber")
                        .description("요청한 페이지 번호")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.requestSize")
                        .description("페이지 크기")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.hasNextPage")
                        .description("다음 페이지가 존재하는지")
                        .type(JsonFieldType.BOOLEAN),
                fieldWithPath("pagination.blockLeft")
                        .description("페이지 목록의 왼쪽")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("pagination.blockRight")
                        .description("페이지 목록의 오른쪽")
                        .type(JsonFieldType.NUMBER)
        );
    }

    public FieldDescriptors problemFields(){
        return new FieldDescriptors(
                fieldWithPath("error.type")
                        .description("사전 정의된 문제 타입")
                        .type(JsonFieldType.STRING),
                fieldWithPath("error.title")
                        .description("문제 이름")
                        .type(JsonFieldType.STRING),
                fieldWithPath("error.status")
                        .description("문제에 대한 http 상태코드")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("error.detail")
                        .description("문제에 대한 자세한 설명")
                        .optional()
                        .type(JsonFieldType.STRING),
                fieldWithPath("error.instance")
                        .description("문제가 발생한 위치")
                        .type(JsonFieldType.STRING)
        );
    }

    public Schema problemSchema(){
        return new Schema("exceptionDetail");
    }

    public HeaderDescriptorWithType authorizationHeader(){
        return new HeaderDescriptorWithType("Authorization").description("액세스토큰");
    }
}
