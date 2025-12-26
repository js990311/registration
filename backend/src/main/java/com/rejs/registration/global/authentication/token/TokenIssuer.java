package com.rejs.registration.global.authentication.token;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

public class TokenIssuer {
    private JwtEncoder jwtEncoder;
    private final long ACCESS_TOKEN_EXPIRY = 30*60L;

    public TokenIssuer(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public Tokens issue(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ACCESS_TOKEN_EXPIRY);

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")                     // 발행자 식별자
                .issuedAt(now)                      // 발행 시간
                .expiresAt(expiresAt)               // 만료 시간
                .subject(username)                  // 사용자 식별값 (Username)
                .claim("role", roles)        // 권한 정보
                .build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new Tokens(
                accessToken,
                expiresAt.toEpochMilli() // 만료 시간(ms) 추가
        );
    }

}
