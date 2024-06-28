package com.dcm.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    @NotNull
    @Size(max = 255)
    private String firstname;

    @NotNull
    @Size(max = 255)
    private String lastname;

    @NotNull
    @Size(max = 255)
    private String function;

    @Size(max = 255)
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(max = 255)
    private String role;

    @NotNull
    private String statut;

    @NotNull
    private String password;
}
