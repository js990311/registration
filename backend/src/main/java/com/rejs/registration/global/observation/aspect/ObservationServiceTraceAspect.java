package com.rejs.registration.global.observation.aspect;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ObservationServiceTraceAspect {
    private final ObservationRegistry observationRegistry;

    @Around("@within(org.springframework.stereotype.Service)")
    public Object traceServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return Observation.createNotStarted("service.operation", observationRegistry)
                .contextualName(className + "#" + methodName)
                .lowCardinalityKeyValue("class", className)
                .lowCardinalityKeyValue("method", methodName)
                .observeChecked(() -> {
                    return joinPoint.proceed();
                });    }
}
