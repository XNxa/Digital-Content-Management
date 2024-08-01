package com.dcm.backend.annotations;

import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.LogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LogEventAspect {

    @Autowired
    LogRepository logRepository;

    @Around("@annotation(LogEvent)")
    public Object logEventToElasticsearch(ProceedingJoinPoint joinPoint) throws
            Throwable {

        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setAction(
                joinPoint.getSignature().getDeclaringType().getSimpleName() + " : " +
                        joinPoint.getSignature().getName());

        log.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

        log.setMessage(Arrays.toString(joinPoint.getArgs()));

        logRepository.save(log).subscribe();

        return joinPoint.proceed();
    }

}
