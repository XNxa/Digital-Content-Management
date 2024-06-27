package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
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

    @NotNull
    private int page;

    @NotNull
    private int size;

    @Size(max = 255)
    private String filename;

    @Size(max = 255)
    @NotNull
    private String category;

    private List<String> keywords;

    private List<Status> status;
}