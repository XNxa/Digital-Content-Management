package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "fileheader-index")
@Mapping(mappingPath = "phonetic_analyser_mapping.json")
@Setting(settingPath = "phonetic_analyser_setting.json")
public class FileHeaderElastic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String folder;

    private String filename;

    private String thumbnailName;

    @Field(type = FieldType.Binary)
    private byte[] thumbnail;

    private String description;

    private String version;

    private Status status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String type;

    private Long size;

    private Collection<String> keywords;

}