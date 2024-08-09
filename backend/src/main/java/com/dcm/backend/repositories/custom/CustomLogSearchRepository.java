package com.dcm.backend.repositories.custom;

import com.dcm.backend.dto.LogFilterDTO;
import com.dcm.backend.entities.Log;
import com.dcm.backend.utils.Couple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomLogSearchRepository {

    Couple<Flux<Log>, Mono<Long>> searchFrom(LogFilterDTO filter);

}
