package com.dcm.backend.dto;

import com.dcm.backend.utils.ErrorMessages;
import com.dcm.backend.validation.constraints.Folder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FilenameDTO {

    @Folder(message = ErrorMessages.INVALID_FOLDER_CODE)
    private String folder;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String filename;

}
