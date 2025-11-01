package com.rejs.registration.global.response;

import com.rejs.registration.global.exception.GlobalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse <T>{
    private final BaseResponseHeader header;
    private final T body;

    public BaseResponse(BaseResponseHeader header, T body) {
        this.header = header;
        this.body = body;
    }

    public static <T> BaseResponse<T> of(HttpStatus status, T body){
        // ok나 created같은 경우는 그대로 내보냄
        return new BaseResponse<>(new BaseResponseHeader(status.value(), status.getReasonPhrase()), body);
    }

    public static <T> BaseResponse<T> fromException(GlobalException exception){
        // 코드는 http를 그대로 사용하되 exception인 경우 custom message를 통해 좀 더 정보를 제공
        return new BaseResponse<>(new BaseResponseHeader(exception.getStatus().value(), exception.getMessage()), null);
    }

    @Getter
    static class BaseResponseHeader{
        private final int status;
        private final String message;

        public BaseResponseHeader(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
