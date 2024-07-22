package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileHeader, Integer>,
        JpaSpecificationExecutor<FileHeader> {

    Optional<FileHeader> findByFolderAndFilename(String folder, String filename);

    @Query("SELECT DISTINCT f.type FROM FileHeader f WHERE f.folder = :folder")
    Collection<String> findTypesByFolder(String folder);

    long countByDateAfterAndFolder(LocalDate dateFrom, String folder);

    long countByStatus(Status status);
}
