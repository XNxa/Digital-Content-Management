package com.dcm.backend.annotations;

import com.dcm.backend.api.FileController;
import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.LogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
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

        Log log =  new Log();
        log.setDate(LocalDateTime.now());
        log.setAction(joinPoint.getSignature().getDeclaringType().getSimpleName() + " : " +
                        joinPoint.getSignature().getName());
        log.setMessage(Arrays.toString(joinPoint.getArgs()));

        try {
            Loggable target = (Loggable) joinPoint.getTarget();
            log.setUser(target.getUser());
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }

        logRepository.save(log);
        
        return joinPoint.proceed();
    }

}
