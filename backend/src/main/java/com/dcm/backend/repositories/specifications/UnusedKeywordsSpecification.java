package com.dcm.backend.repositories.specifications;

import com.dcm.backend.entities.Keyword;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UnusedKeywordsSpecification implements Specification<Keyword> {

    @Override
    public Predicate toPredicate(Root<Keyword> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isEmpty(root.get("fileHeaders"));
    }

}
