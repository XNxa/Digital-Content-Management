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
import java.util.Optional;

@AllArgsConstructor
public class FileFilterSpecification implements Specification<FileHeader> {

    Optional<String> filename;
    Optional<List<Keyword>> keywords;
    Optional<List<Status>> status;

    @Override
    public Predicate toPredicate(Root<FileHeader> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        filename.ifPresent(name -> predicates.add(
                criteriaBuilder.like(root.get("filename"), "%" + name + "%")));

        keywords.ifPresent(kws -> {
            for (Keyword kw : kws) {
                predicates.add(criteriaBuilder.isMember(kw, root.get("keywords")));
            }
        });
        
        status.ifPresent(sts -> predicates.add(
                criteriaBuilder.or(sts.stream()
                        .map(st -> criteriaBuilder.equal(root.get("status"), st))
                        .toArray(Predicate[]::new))
        ));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
