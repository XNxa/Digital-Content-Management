package com.dcm.backend.entities.elastic;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "fileheader-index")
public class FileHeaderElastic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String folder;

    private String filename;

    private String thumbnailName;

    private String description;

    private String version;

    private Status status;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String type;

    private Long size;

    private Collection<String> keywords;

}