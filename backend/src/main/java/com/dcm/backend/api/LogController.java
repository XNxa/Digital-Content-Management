package com.dcm.backend.api;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.services.LogService;
import com.dcm.backend.utils.Permissions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    @Autowired
    LogService logService;

    @PreAuthorize("hasRole('" + Permissions.LOGS_CONSULT + "')")
    @GetMapping("/count")
    public long getLogs() {
        return logService.count();
    }

    @PreAuthorize("hasRole('" + Permissions.LOGS_CONSULT + "')")
    @GetMapping("/list")
    public Collection<LogDTO> getLogs(int first, int numberOfElements) {
        return logService.list(first, numberOfElements);
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
