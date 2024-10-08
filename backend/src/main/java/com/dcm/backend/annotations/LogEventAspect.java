package com.dcm.backend.annotations;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.FileRepository;
import com.dcm.backend.repositories.LogRepository;
import com.dcm.backend.utils.mappers.FileHeaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dcm.backend.utils.Logger;

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

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetLogClass = logevent.targetLogClass();
        boolean isReturnTypeAssignable =
                targetLogClass.equals(methodSignature.getReturnType());
        boolean isAnyParameterTypeAssignable =
                Arrays.asList(methodSignature.getParameterTypes())
                        .contains(targetLogClass);

        if (isReturnTypeAssignable) {
            afterSpiedObject = res;
        }

        if (isAnyParameterTypeAssignable) {
            afterSpiedObject = Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> arg.getClass().equals(targetLogClass))
                    .findFirst()
                    .get();
        }
    
        log.setBefore(Logger.toString(beforeSpiedObject));
        log.setAfter(Logger.toString(afterSpiedObject));

        beforeSpiedObject = null;
        afterSpiedObject = null;

        logRepository.save(log).subscribe();
        return res;
    }

    @Around(value = "(execution(* com.dcm.backend.repositories.*.save(..)) || execution(* com.dcm.backend.repositories.*.delete(..))) && args(com.dcm.backend.entities.FileHeader,..)")
    public Object aroundRepositoryAction(ProceedingJoinPoint jp) throws Throwable {
        if (spyOnRepositories) {
            FileHeader arg = (FileHeader) Arrays.stream(jp.getArgs())
                    .filter(o -> o instanceof FileHeader)
                    .findFirst()
                    .orElse(null);
            assert arg != null;

            MethodSignature m = (MethodSignature) jp.getSignature();
            switch (m.getName()) {
                case "save":
                    beforeSpiedObject = fileHeaderMapper.copy(
                            fileRepository.findById(arg.getId()).orElse(null));
                    Object res = jp.proceed();
                    afterSpiedObject = fileHeaderMapper.copy((FileHeader) res);
                    return res;
                case "delete":
                    beforeSpiedObject = fileHeaderMapper.copy(arg);
                    afterSpiedObject = null;
                    break;
                default:
                    log.error("Unexpected use of @LogEvent");
            }
        }
        return jp.proceed();
    }

    @AfterReturning(value = "execution(* com.dcm.backend.services.impl.UserServiceImpl.delete(." +
            ".) || execution(* com.dcm.backend.services.impl.RoleServiceImpl.delete(..)" )
    public void logDelete(JoinPoint jp) {
        assert jp.getArgs().length == 1;
        beforeSpiedObject = "Deleted object with id=" + jp.getArgs()[0];
    }
}
