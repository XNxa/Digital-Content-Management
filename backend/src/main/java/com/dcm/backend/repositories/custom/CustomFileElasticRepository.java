package com.dcm.backend.repositories.custom;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.entities.FileHeaderElastic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CustomFileElasticRepository {

    Mono<Long> countByFilter(FileFilterDTO filter);

    Flux<SearchHit<FileHeaderElastic>> findByFilter(FileFilterDTO filter, Pageable pageable);

    Flux<SearchHit<FileHeaderElastic>> searchByQuery(String query,
                                                     Optional<String> folder,
                                                     Pageable pageable);
}
