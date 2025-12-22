package com.rejs.registration.global.observation.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationTextPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ObservationConfig {
    @Profile("dev")
    @Bean
    public ObservationHandler<Observation.Context> observationHandler(){
        return new ObservationTextPublisher();
    }
}
