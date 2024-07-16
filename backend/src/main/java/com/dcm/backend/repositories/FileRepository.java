package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileHeader, Integer>,
        JpaSpecificationExecutor<FileHeader> {

    Optional<FileHeader> findByFolderAndFilename(String folder, String filename);

}
