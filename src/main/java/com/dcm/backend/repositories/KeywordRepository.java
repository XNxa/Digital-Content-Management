package com.dcm.backend.repositories;

import com.dcm.backend.beans.Keyword;
import org.springframework.data.repository.CrudRepository;

public interface KeywordRepository extends CrudRepository<Keyword, String> {
}
