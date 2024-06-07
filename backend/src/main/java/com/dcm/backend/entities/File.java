package com.dcm.backend.entities;

import com.dcm.backend.enumeration.Status;
import com.dcm.backend.enumeration.Version;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class File {

    @Id
    private String filename;

    private String description;

    private Version version;

    private Status status;

    private String date;

    private String type;

    private Long size;

    @ManyToMany
    private Collection<Keyword> keywords;

}