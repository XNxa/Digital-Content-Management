package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

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

    @SuppressWarnings("CopyConstructorMissesField")
    public FileHeader(@NotNull FileHeader fileHeader) {
        this.filename = fileHeader.getFilename();
        this.thumbnailName = fileHeader.getThumbnailName();
        this.description = fileHeader.getDescription();
        this.version = fileHeader.getVersion();
        this.status = fileHeader.getStatus();
        this.date = fileHeader.getDate();
        this.type = fileHeader.getType();
        this.size = fileHeader.getSize();
        this.keywords = fileHeader.getKeywords();
    }
}