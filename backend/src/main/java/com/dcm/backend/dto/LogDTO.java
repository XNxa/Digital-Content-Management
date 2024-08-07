package com.dcm.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LogDTO {

    private LocalDateTime date;

    private String user;

    private String action;

    private String before;

    private String after;

}
