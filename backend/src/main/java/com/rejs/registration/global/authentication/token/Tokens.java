package com.rejs.registration.global.authentication.token;

import lombok.Getter;

@Getter
public class Tokens {
    private final String accessToken;
    private final long accessTokenExpiresAt;

    public Tokens(String accessToken, long accessTokenExpiresAt) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }
}
