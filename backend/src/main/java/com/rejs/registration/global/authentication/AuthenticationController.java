package com.rejs.registration.global.authentication;

import com.rejs.registration.global.authentication.claims.annotation.TokenClaim;
import com.rejs.registration.global.exception.BusinessException;
import com.rejs.registration.global.problem.ProblemCode;
import com.rejs.registration.global.response.BaseResponse;
import com.rejs.token_starter.token.ClaimsDto;
import com.rejs.token_starter.token.Tokens;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/refresh")
    public Tokens refresh(@TokenClaim ClaimsDto claim){
        return authenticationService.refresh(claim);
    }
}
