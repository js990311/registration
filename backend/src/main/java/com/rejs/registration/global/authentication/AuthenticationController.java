package com.rejs.registration.global.authentication;

import com.rejs.registration.global.response.BaseResponse;
import com.rejs.token_starter.token.Tokens;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public Tokens login(@RequestBody LoginRequest request){
        Tokens tokens = authenticationService.login(request);
        return tokens;
    }

    @PostMapping("signup")
    public ResponseEntity<Tokens> signup(@RequestBody LoginRequest request){
        Tokens tokens = authenticationService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }
}
