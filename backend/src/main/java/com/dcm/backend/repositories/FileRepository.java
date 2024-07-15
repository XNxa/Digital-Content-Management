package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileHeader, Integer>,
        JpaSpecificationExecutor<FileHeader> {

    Optional<FileHeader> findByFilename(String filename);

    @Query("select distinct f.type from FileHeader f where f.filename like :prefix%") // TODO
    Collection<String> findAllDistinctTypes(@Param("prefix") String folder);

}
