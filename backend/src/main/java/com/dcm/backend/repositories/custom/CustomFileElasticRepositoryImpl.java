package com.dcm.backend.repositories.custom;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.entities.elastic.FileHeaderElastic;
import com.dcm.backend.enumeration.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class CustomFileElasticRepositoryImpl implements CustomFileElasticRepository {

    @Autowired
    private ElasticsearchOperations elasticsearch;

    @Override
    public long countByFilter(FileFilterDTO filter) {
        Query query = buildFilterQuery(filter);
        return elasticsearch.count(query, FileHeaderElastic.class);
    }

    @Override
    public SearchHits<FileHeaderElastic> findByFilter(FileFilterDTO filter, Pageable pageable) {
        Query query = buildFilterQuery(filter).setPageable(pageable);
        System.out.println(query.getFields());
        return elasticsearch.search(query, FileHeaderElastic.class);
    }

    private Query buildFilterQuery(FileFilterDTO filter) {
        Criteria criteria = new Criteria();

        if (filter.getFolder() != null && !filter.getFolder().isBlank()) {
            criteria = criteria.and("folder").is(filter.getFolder());
        }

        if (filter.getFilename() != null && !filter.getFilename().isBlank()) {
            criteria = criteria.and("filename").contains(filter.getFilename());
        }

        if (filter.getKeywords() != null && !filter.getKeywords().isEmpty()) {
            Criteria keywordCriteria = new Criteria();
            for (String keyword : filter.getKeywords()) {
                keywordCriteria = keywordCriteria.or("keywords").contains(keyword);
            }
            criteria = criteria.subCriteria(keywordCriteria);
        }

        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            Criteria statusCriteria = new Criteria();
            for (Status status : filter.getStatus()) {
                statusCriteria = statusCriteria.or("status").is(status);
            }
            criteria = criteria.subCriteria(statusCriteria);
        }

        if (filter.getVersion() != null && !filter.getVersion().isBlank()) {
            criteria = criteria.and("version").contains(filter.getVersion());
        }

        if (filter.getType() != null) {
            Criteria typeCriteria = new Criteria();
            for (String type : filter.getType()) {
                typeCriteria = typeCriteria.or("type").is(type);
            }
            criteria = criteria.subCriteria(typeCriteria);
        }

        if (filter.getDateFrom() != null) {
            criteria = criteria.and("date").greaterThanEqual(filter.getDateFrom());
        }

        if (filter.getDateTo() != null) {
            criteria = criteria.and("date").lessThanEqual(filter.getDateTo());
        }

        return new CriteriaQuery(criteria);
    }

}
