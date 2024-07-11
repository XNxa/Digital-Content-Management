package com.dcm.backend.dto;

import com.dcm.backend.utils.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private String id;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String firstname;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String lastname;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String function;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @Email(message = ErrorMessages.INVALID_EMAIL_CODE)
    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String email;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String role;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String statut;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String password;
}
