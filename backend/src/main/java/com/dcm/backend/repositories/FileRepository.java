package com.dcm.backend.repositories;

import com.dcm.backend.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {

}
