package com.dcm.backend.services;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.dto.LogFilterDTO;
import com.dcm.backend.utils.PaginatedResponse;

public interface LogService {

    /**
     * Returns a list of logs.
     *
     * @param filterDTO the filter to apply
     * @return a list of logs
     */
    PaginatedResponse<LogDTO> list(LogFilterDTO filterDTO);

    /**
     * Logs a user log in.
     *
     * @param ip the ip address of the user
     */
    void logLogIn(String ip);

    /**
     * Logs a user log out.
     */
    void logLogOut();
}
