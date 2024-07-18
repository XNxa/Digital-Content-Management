package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeader;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileElasticRepository extends ElasticsearchRepository<FileHeader,
        Integer> {

}
