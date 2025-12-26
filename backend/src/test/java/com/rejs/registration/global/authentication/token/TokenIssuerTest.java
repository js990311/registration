package com.rejs.registration.global.authentication.token;

import com.rejs.registration.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TokenIssuerTest {
    @Autowired
    private TokenIssuer tokenIssuer;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void issue() {
        String username = "testuser";
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));

        // when
        Tokens tokens = tokenIssuer.issue(username, authorities);

        // then
        assertNotNull(tokens.getAccessToken());
        Jwt decodedJwt = jwtDecoder.decode(tokens.getAccessToken());
        assertEquals(username, decodedJwt.getSubject());
        assertEquals(username, decodedJwt.getSubject());
        assertTrue(decodedJwt.getClaimAsString("role").contains("ROLE_STUDENT"));
        assertTrue(decodedJwt.getExpiresAt().isAfter(Instant.now()));
    }
}