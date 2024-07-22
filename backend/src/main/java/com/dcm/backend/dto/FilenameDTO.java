package com.dcm.backend.dto;

import com.dcm.backend.utils.ErrorMessages;
import com.dcm.backend.validation.constraints.Folder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilenameDTO {

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    @Folder(message = ErrorMessages.INVALID_FOLDER_CODE)
    private String folder;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String filename;

}
