package com.rejs.registration.global.authentication;

import com.rejs.registration.global.authentication.dto.LoginRequest;
import com.rejs.registration.global.authentication.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authenticationService.login(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("signup")
    public LoginResponse signup(@RequestBody LoginRequest request){
        return authenticationService.signup(request);
    }

}
