package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileHeader, Integer> {

}
