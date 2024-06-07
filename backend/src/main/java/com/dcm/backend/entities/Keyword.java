package com.dcm.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Keyword {

    @Id
    private String name;

    @ManyToMany(mappedBy = "keywords")
    private Collection<File> files;

    public Keyword(String key) {
        this.name = key;
    }

}
