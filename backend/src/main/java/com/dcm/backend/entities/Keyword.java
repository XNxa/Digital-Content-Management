package com.dcm.backend.entities;

import com.dcm.backend.annotations.LogIgnore;
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

    @LogIgnore
    @ManyToMany(mappedBy = "keywords")
    private Collection<FileHeader> fileHeaders;

    public Keyword(String key) {
        this.name = key;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Keyword(Keyword keyword) {
        this.name = keyword.getName();
    }

    @Override
    public String toString() {
        return "Keyword{" + "name='" + name + '\'' + '}';
    }
}
