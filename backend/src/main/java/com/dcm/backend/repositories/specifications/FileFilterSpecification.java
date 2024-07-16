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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FileFilterSpecification implements Specification<FileHeader> {

    private String folder;
    private String filename;
    private List<Keyword> keywords;
    private List<Status> status;
    private String version;
    private String type;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    @Override
    public Predicate toPredicate(Root<FileHeader> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.like(root.get("folder"), folder));

        if (!filename.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(root.get("filename"), "%" + filename + "%"));
        }

        if (!keywords.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (Keyword kw : keywords) {
                keywordPredicates.add(criteriaBuilder.isMember(kw, root.get("keywords")));
            }
            predicates.add(
                    criteriaBuilder.or(keywordPredicates.toArray(new Predicate[0])));
        }

        if (!status.isEmpty()) {
            List<Predicate> statusPredicates = new ArrayList<>();
            for (Status sts : status) {
                statusPredicates.add(criteriaBuilder.equal(root.get("status"), sts));
            }
            predicates.add(
                    criteriaBuilder.or(statusPredicates.toArray(new Predicate[0])));
        }

        if (version != null && !version.isBlank()) {
            predicates.add(
                    criteriaBuilder.like(root.get("version"), "%" + version + "%"));
        }

        if (type != null && !type.isBlank()){
            predicates.add(criteriaBuilder.like(root.get("type"), "%" + type + "%"));
        }

        if (dateFrom != null) {
            System.out.println(dateFrom);
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }

        if (dateTo != null) {
            System.out.println(dateTo);
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
