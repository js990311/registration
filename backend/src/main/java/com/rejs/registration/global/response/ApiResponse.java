package com.rejs.registration.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public static <T> ApiResponse of(T data){
        return new ApiResponse(data);
    }
}
