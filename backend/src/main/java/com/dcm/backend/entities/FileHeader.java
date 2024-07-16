package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private String date;

    private String type;

    private Long size;

    @ManyToMany
    private Collection<Keyword> keywords;
    
}