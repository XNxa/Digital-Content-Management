package com.dcm.backend.repositories.specifications;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FileFilterSpecification implements Specification<FileHeader> {

    String filename;
    List<Keyword> keywords;
    List<Status> status;

    @Override
    public Predicate toPredicate(Root<FileHeader> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!filename.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(root.get("filename"), "%" + filename + "%"));
        }

        if (!keywords.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (Keyword kw : keywords) {
                keywordPredicates.add(criteriaBuilder.isMember(kw, root.get("keywords")));
            }
            predicates.add(criteriaBuilder.or(keywordPredicates.toArray(new Predicate[0])));
        }

        if (!status.isEmpty()) {
            List<Predicate> statusPredicates = new ArrayList<>();
            for (Status sts : status) {
                statusPredicates.add(criteriaBuilder.equal(root.get("status"), sts));
            }
            predicates.add(
                    criteriaBuilder.or(statusPredicates.toArray(new Predicate[0])));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
