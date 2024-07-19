package com.dcm.backend.repositories.custom;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.entities.FileHeaderElastic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomFileElasticRepository {

    long countByFilter(FileFilterDTO filter);

    SearchHits<FileHeaderElastic> findByFilter(FileFilterDTO filter, Pageable pageable);

}
