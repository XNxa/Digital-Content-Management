package com.dcm.backend.repositories;

import com.dcm.backend.entities.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogRepository extends ElasticsearchRepository<Log, Long> {
}
