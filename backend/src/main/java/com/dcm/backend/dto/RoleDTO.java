package com.dcm.backend.dto;

import com.dcm.backend.utils.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

@Data
public class RoleDTO {

    private String id;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String name;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String description;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private boolean state;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private Collection<String> permissions;

}
