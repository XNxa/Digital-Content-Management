package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.validation.constraints.ValidMimeType;
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

    @NotNull
    private String filename;

    private String thumbnailName;

    @Size(max = 255)
    @NotNull
    private String description;

    @Size(max = 255)
    @NotNull
    private String version;

    @NotNull
    private Status status;

    private String date;

    @ValidMimeType
    @NotNull
    private String type;

    @NotNull
    private Long size;

    @NotNull
    private Collection<String> keywords;
}
