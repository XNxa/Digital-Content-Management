package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.utils.ErrorMessages;
import com.dcm.backend.validation.constraints.Folder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    @Folder(message = ErrorMessages.INVALID_FOLDER_CODE)
    private String folder;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String filename;

    private List<String> keywords;

    private List<Status> status;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private String version;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    private List<String> type;

    private LocalDate dateFrom;

    private LocalDate dateTo;
}