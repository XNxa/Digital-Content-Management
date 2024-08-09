package com.dcm.backend.repositories.custom;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.dcm.backend.dto.LogFilterDTO;
import com.dcm.backend.entities.Log;
import com.dcm.backend.utils.Couple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveSearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomLogSearchRepositoryImpl implements CustomLogSearchRepository {

    @Autowired
    private ReactiveElasticsearchOperations elasticsearch;

    @Override
    public Couple<Flux<Log>, Mono<Long>> searchFrom(LogFilterDTO filter) {
        Query query = createQuery(filter);

        Mono<ReactiveSearchHits<Log>> searchHitsMono =
                elasticsearch.searchForHits(query, Log.class);

        Mono<Long> totalHitsMono = searchHitsMono.map(ReactiveSearchHits::getTotalHits);
        var logsFlux = searchHitsMono.flatMapMany(ReactiveSearchHits::getSearchHits)
                .map(SearchHit::getContent);

        return new Couple<>(logsFlux, totalHitsMono);
    }

    /**
     * Return logs from 'filter.getPage()' page with 'filter.getSize();' results
     * sorting by 'date' and if criterias are not null :<br/>
     * - if 'filter.getSearch()' != null, match on all fields<br/>
     * - if 'filter.getDateFrom()' != null, only logs after this LocalDate<br/>
     * - if 'filter.getDateTo()' != null, only logs before this LocalDate
     */
    private Query createQuery(LogFilterDTO filter) {

        NativeQueryBuilder builder = new NativeQueryBuilder();

        builder.withSort(Sort.by(Sort.Direction.DESC, "date"))
                .withPageable(PageRequest.of(filter.getPage(), filter.getSize()));

        builder.withQuery(q -> q.bool(b -> {
            if (filter.getSearch() != null) b.must(m -> m.multiMatch(
                    mm -> mm.query(filter.getSearch())
                            .type(TextQueryType.PhrasePrefix)
                            .fields("user", "action", "before", "after")));

            if (filter.getDateFrom() != null) b.filter(f -> f.range(r -> r.field("date")
                    .from(LocalDateTime.of(filter.getDateFrom(), LocalTime.of(0, 0, 1))
                            .toString())));

            if (filter.getDateTo() != null) b.filter(f -> f.range(r -> r.field("date")
                    .to(LocalDateTime.of(filter.getDateTo(), LocalTime.of(23, 59, 59))
                            .toString())));

            return b;
        }));

        return builder.build();
    }

}
