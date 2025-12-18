package com.rejs.registration.global.response.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.registration.global.response.ApiExceptionResponse;
import com.rejs.registration.global.response.ApiPageResponse;
import com.rejs.registration.global.response.ApiResponse;
import com.rejs.registration.global.response.ExceptionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.rejs.registration")
@RequiredArgsConstructor
public class ResponseWrapper implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        if(parameterType.equals(void.class)){
            return false;
        }
        if(parameterType.equals(ResponseEntity.class)){
            ResolvableType resolvableType = ResolvableType.forMethodParameter(returnType);
            ResolvableType genericType = resolvableType.getGeneric(0);
            if (genericType.resolve() == Void.class) {
                return false;
            }
        }
        if(
                parameterType.equals(ApiResponse.class) ||
                parameterType.equals(ApiPageResponse.class) ||
                parameterType.equals(ApiExceptionResponse.class)
        ){

        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof ExceptionDetail exceptionDetail){
            return new ApiExceptionResponse(exceptionDetail);
        }

        if(body instanceof Page<?> page){
            return ApiPageResponse.of(page);
        }

        return ApiResponse.of(body);
    }
}
