package com.dcm.backend.dto;

import com.dcm.backend.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileHeaderDTO {

    private String filename;

    private String description;

    private String version;

    private Status status;

    private String date;

    private String type;

    private Long size;

    private Collection<String> keywords;

}
