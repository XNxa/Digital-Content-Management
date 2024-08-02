package com.dcm.backend.annotations;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.LogRepository;
import com.dcm.backend.utils.mappers.FileHeaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogEventAspect {

    @Autowired
    LogRepository logRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileHeaderMapper fileHeaderMapper;

    private boolean spyOnRepositories = false;
    private Object beforeSpiedObject = null;
    private Object afterSpiedObject = null;

    @Around("@annotation(logevent)")
    public Object logEventToElasticsearch(ProceedingJoinPoint joinPoint, LogEvent logevent) throws
            Throwable {

        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setAction(joinPoint.getSignature()
                .getDeclaringType()
                .getSimpleName() + " : " + joinPoint.getSignature().getName());

        log.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

        spyOnRepositories = true;
        Object res = joinPoint.proceed();
        spyOnRepositories = false;

        log.setBefore(beforeSpiedObject == null ? "null" : beforeSpiedObject.toString());
        log.setAfter(afterSpiedObject == null ? "null" : afterSpiedObject.toString());

        logRepository.save(log).subscribe();
        beforeSpiedObject = null;
        afterSpiedObject = null;
        return res;
    }

    @Around(value = "execution(* com.dcm.backend.repositories.*.*(..)) && " + "args(com" + ".dcm.backend.entities.FileHeader)")
    public Object logEventToElasticsearchBefore(ProceedingJoinPoint jp) throws Throwable {
        if (spyOnRepositories) {
            FileHeader arg = (FileHeader) Arrays.stream(jp.getArgs())
                    .filter(o -> o instanceof FileHeader)
                    .findFirst()
                    .orElse(null);
            assert arg != null;

            MethodSignature m = (MethodSignature) jp.getSignature();
            switch (m.getName()) {
                case "save":
                    beforeSpiedObject =
                            fileHeaderMapper.copy(fileRepository.findById(arg.getId()).orElse(null));
                    Object res = jp.proceed();
                    afterSpiedObject = fileHeaderMapper.copy((FileHeader) res);
                    return res;
                case "delete":
                    beforeSpiedObject = fileHeaderMapper.copy(arg);
                    afterSpiedObject = null;
                    break;
                default:
                    log.error("Unexpected use of LogEvent");
            }
        }
        return jp.proceed();
    }

}
