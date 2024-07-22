package com.dcm.backend.repositories;

import com.dcm.backend.entities.FileHeaderElastic;
import com.dcm.backend.repositories.custom.CustomFileElasticRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;

public interface FileElasticRepository extends ElasticsearchRepository<FileHeaderElastic,
        Integer>, CustomFileElasticRepository {

}
