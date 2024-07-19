package com.dcm.backend.repositories;

import com.dcm.backend.entities.elastic.FileHeaderElastic;
import com.dcm.backend.repositories.custom.CustomFileElasticRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileElasticRepository extends ElasticsearchRepository<FileHeaderElastic,
        Integer>, CustomFileElasticRepository {

}
