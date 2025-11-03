package com.rejs.registration.global.config;

import com.rejs.registration.global.authentication.claims.resolver.TokenClaimArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TokenClaimArgumentResolver tokenClaimArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(tokenClaimArgumentResolver);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
