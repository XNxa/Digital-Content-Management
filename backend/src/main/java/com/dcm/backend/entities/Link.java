package com.dcm.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.ConstraintMode.CONSTRAINT;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;

    private LocalDateTime expirationDate;

    @ManyToOne
    @JoinColumn(name = "id", foreignKey = @ForeignKey(value = CONSTRAINT,
            foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES file_header(id) ON " +
                    "DELETE CASCADE")) // Delete the link when the file is deleted
    private FileHeader file;

}
