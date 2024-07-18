package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.util.Collection;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "fileheader-index")
public class FileHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String folder;

    private String filename;

    private String thumbnailName;

    private String description;

    private String version;

    private Status status;

    private LocalDate date;

    private String type;

    private Long size;

    @ManyToMany
    private Collection<Keyword> keywords;
    
}