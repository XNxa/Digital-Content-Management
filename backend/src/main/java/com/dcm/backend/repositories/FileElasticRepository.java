package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeaderElastic;
import com.dcm.backend.repositories.custom.CustomFileElasticRepository;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface FileElasticRepository extends ReactiveElasticsearchRepository<FileHeaderElastic,
        Integer>, CustomFileElasticRepository {
}
