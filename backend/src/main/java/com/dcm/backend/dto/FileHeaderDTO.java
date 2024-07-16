package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.utils.ErrorMessages;
import com.dcm.backend.validation.constraints.Folder;
import com.dcm.backend.validation.constraints.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileHeaderDTO {

    @Folder(message = ErrorMessages.INVALID_FOLDER_CODE)
    private String folder;

    @NotBlank(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String filename;

    private String thumbnailName;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotNull(message =ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String description;

    @Size(max = 255, message = ErrorMessages.EXCEED_MAX_SIZE_CODE)
    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String version;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private Status status;

    private String date;

    @MimeType(message = ErrorMessages.INVALID_MIME_TYPE_CODE)
    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private String type;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private Long size;

    @NotNull(message = ErrorMessages.REQUIERED_FIELD_MISSING_CODE)
    private Collection<String> keywords;
}
