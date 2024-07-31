package com.dcm.backend.repositories;

import com.dcm.backend.entities.Log;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface LogRepository extends ReactiveElasticsearchRepository<Log, Long> {
}
