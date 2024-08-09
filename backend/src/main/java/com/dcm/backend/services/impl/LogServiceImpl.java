package com.dcm.backend.services.impl;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.dto.LogFilterDTO;
import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.LogRepository;
import com.dcm.backend.services.LogService;
import com.dcm.backend.utils.Couple;
import com.dcm.backend.utils.PaginatedResponse;
import com.dcm.backend.utils.mappers.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    LogMapper logMapper;

    @Override
    public PaginatedResponse<LogDTO> list(LogFilterDTO logFilterDTO) {
        Couple<Flux<Log>, Mono<Long>> couple = logRepository.searchFrom(logFilterDTO);
        return new PaginatedResponse<>(
                couple.first().map(logMapper::toDTO).collectList().block(),
                couple.second().block());
    }

    @Override
    public void logLogIn(String ip) {
        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
        log.setAction("User logged in with ip : " + ip);
        logRepository.save(log).subscribe();
    }

    @Override
    public void logLogOut() {
        Log log = new Log();
        log.setDate(LocalDateTime.now());
        log.setUser(SecurityContextHolder.getContext().getAuthentication().getName());
        log.setAction("User logged out");
        logRepository.save(log).subscribe();
    }

}
