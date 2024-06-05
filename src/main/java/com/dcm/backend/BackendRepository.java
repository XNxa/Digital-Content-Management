package com.dcm.backend;

import com.dcm.backend.beans.File;
import org.springframework.data.repository.CrudRepository;

public interface BackendRepository extends CrudRepository<File, Integer> {

}
