package com.rejs.registration.global.authentication.dto;

import com.rejs.registration.domain.student.dto.StudentDto;
import com.rejs.registration.global.authentication.token.Tokens;
import lombok.Getter;

@Getter
public class LoginResponse {
    private Tokens tokens;
    private String username;

    public LoginResponse(Tokens tokens, String username) {
        this.tokens = tokens;
        this.username = username;
    }
}
