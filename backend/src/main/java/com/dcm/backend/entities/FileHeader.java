package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    public FileHeader(String filename, String description, String version, Status status, String date, String type, Long size, Collection<Keyword> keywords) {
        this.filename = filename;
        this.description = description;
        this.version = version;
        this.status = status;
        this.date = date;
        this.type = type;
        this.size = size;
        this.keywords = keywords;
    }
}