package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileHeader, Integer> {

    Optional<FileHeader> findByFilename(String filename);
    
}
