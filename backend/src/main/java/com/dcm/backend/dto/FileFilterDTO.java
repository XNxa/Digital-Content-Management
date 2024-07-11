package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.utils.ErrorMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileFilterDTO {

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private int page;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private int size;

    @Size(max = 255,  message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String filename;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String category;

    private List<String> keywords;

    private List<Status> status;
}