package com.rejs.registration.global.observation.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationTextPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ObservationConfig {
/*
    // 사실 dev 환경에서도 trace를 볼 이유는 없다. 로그만 더러워질뿐
    @Profile("dev")
    @Bean
    public ObservationHandler<Observation.Context> observationHandler(){
        return new ObservationTextPublisher();
    }
*/
}

