package com.dcm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class CredentialsDTO {

    @NotBlank
    private String username;

    @ToString.Exclude
    @NotBlank
    private String password;
}
