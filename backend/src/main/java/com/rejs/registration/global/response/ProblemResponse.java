package com.rejs.registration.global.response;

import com.rejs.registration.global.problem.ProblemCode;
import lombok.Getter;

@Getter
public class ProblemResponse {
    private String type;
    private String title;
    private Integer status;
    private String instance;
    private String detail;

    public ProblemResponse(ProblemCode code, String instance, String detail ) {
        this.type = code.getType();
        this.title = code.getTitle();
        this.status = code.getStatus().value();
        this.instance = instance;
        if(detail == null){
            this.detail = code.getTitle();
        }else {
            this.detail = detail;
        }
    }
}
