package com.dcm.backend.services.impl;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.repositories.LogRepository;
import com.dcm.backend.services.LogService;
import com.dcm.backend.utils.mappers.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    LogMapper logMapper;

    @Override
    public Collection<LogDTO> list(int first, int numberOfElements) {
        Pageable pageable = PageRequest.of(first, numberOfElements,
                Sort.by(Sort.Direction.DESC, "date"));
        return logRepository.findAllBy(pageable).map(logMapper::toDTO).collectList().block();
    }

    @Override
    public long count() {
        return logRepository.count().block();
    }

}
