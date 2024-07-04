package com.dcm.backend.dto;

import lombok.Data;

@Data
public class PermissionDTO {

    private String folder;

    private String subfolder;

    private String name;

    private String permission;

    // won't be send to frontend
    private int position;

}
