package com.dcm.backend.annotations;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Loggable {

    @Autowired
    private HttpServletRequest request;

    public String getUser() {
        return request.getRemoteUser();
    }

}
