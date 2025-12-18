package com.rejs.registration.global.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ApiPageResponse <T>{
    private T data;
    private PaginationInfo pagination;

    public ApiPageResponse(T data, PaginationInfo pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> ApiPageResponse of(Page<T> data){
        return new ApiPageResponse(
            data.getContent(), PaginationInfo.of(data)
        );
    }
}
