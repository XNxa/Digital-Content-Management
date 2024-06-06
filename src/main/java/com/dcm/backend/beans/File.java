package com.dcm.backend.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

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

    private String Type;

    private Long size;

    @ManyToMany
    @JsonIgnore
    private Collection<Keyword> keywords;

}
