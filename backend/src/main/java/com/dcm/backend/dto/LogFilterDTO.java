package com.dcm.backend.dto;

import com.dcm.backend.utils.ErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogFilterDTO {

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private int page;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private int size;

    private String search;

    private LocalDate dateFrom;

    private LocalDate dateTo;

}
