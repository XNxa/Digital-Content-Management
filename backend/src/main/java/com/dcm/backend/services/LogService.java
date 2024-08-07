package com.dcm.backend.services;

import com.dcm.backend.dto.LogDTO;

import java.util.Collection;

public interface LogService {

    Collection<LogDTO> list(int first, int numberOfElements);

    long count();
}
