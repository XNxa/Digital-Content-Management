package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.enumeration.Version;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Data
public class FileUploadDTO {

    @NotNull
    private MultipartFile file;

    private String description;

    @NotEmpty
    private Version version;

    @NotEmpty
    private Status status;

    private Collection<String> keywords;

}
