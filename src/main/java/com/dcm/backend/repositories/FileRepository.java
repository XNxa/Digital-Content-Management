package com.dcm.backend.repositories;

import com.dcm.backend.beans.File;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, String> {

}
