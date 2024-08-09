package com.dcm.backend.api;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.dto.LogFilterDTO;
import com.dcm.backend.services.LogService;
import com.dcm.backend.utils.PaginatedResponse;
import com.dcm.backend.utils.Permissions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    @Autowired
    LogService logService;

    @PreAuthorize("hasRole('" + Permissions.LOGS_CONSULT + "')")
    @GetMapping("/list")
    public PaginatedResponse<LogDTO> getLogs(@Valid @ModelAttribute LogFilterDTO filterDTO) {
        return logService.list(filterDTO);
    }

    @GetMapping("/connected")
    public void userLogIn(HttpServletRequest req) {
        logService.logLogIn(req.getRemoteAddr());
    }

    @GetMapping("/disconnected")
    public void userLogOut() {
        logService.logLogOut();
    }

}
