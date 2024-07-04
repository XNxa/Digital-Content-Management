package com.dcm.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

@Data
public class RoleDTO {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private String description;

    @NotNull
    private boolean state;

    @NotNull
    private Collection<String> permissions;

}
