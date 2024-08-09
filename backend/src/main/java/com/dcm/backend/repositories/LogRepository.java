package com.dcm.backend.repositories;

import com.dcm.backend.entities.Log;
import com.dcm.backend.repositories.custom.CustomLogSearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

public interface LogRepository extends ReactiveElasticsearchRepository<Log, Long>,
        CustomLogSearchRepository {

    Flux<Log> findAllBy(Pageable pageable);

}
