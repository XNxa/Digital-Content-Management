package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileFilterDTO {

    @NotNull
    private int page;

    @NotNull
    private int size;

    @Size(max = 255)
    private String filename;

    private List<String> keywords;

    private List<Status> status;
}