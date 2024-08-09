package com.dcm.backend.entities;

import com.dcm.backend.annotations.LogIgnore;
import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileHeader {

    @Id
    @LogIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String folder;

    private String filename;

    @LogIgnore
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