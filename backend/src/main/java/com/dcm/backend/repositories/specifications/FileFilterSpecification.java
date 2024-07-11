package com.dcm.backend.repositories.specifications;

import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.Keyword;
import com.dcm.backend.enumeration.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class FileFilterSpecification implements Specification<FileHeader> {

    String filename;
    List<Keyword> keywords;
    List<Status> status;
    String category;

    private final static String[] imageTypes =
            {"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
                    "image/tiff",};

    private final static String[] videoTypes = {"video/%",};

    private final static String[] pictoTypes = {"image/svg+xml",};


    @Override
    public Predicate toPredicate(@NotNull Root<FileHeader> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
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

        switch (category) {
            case "images":
                predicates.add(criteriaBuilder.or(Arrays.stream(imageTypes)
                        .map(type -> criteriaBuilder.like(root.get("type"), type))
                        .toArray(Predicate[]::new)));
                break;
            case "videos":
                predicates.add(criteriaBuilder.or(Arrays.stream(videoTypes)
                        .map(type -> criteriaBuilder.like(root.get("type"), type))
                        .toArray(Predicate[]::new)));
                break;
            case "pictos":
                predicates.add(criteriaBuilder.or(Arrays.stream(pictoTypes)
                        .map(type -> criteriaBuilder.like(root.get("type"), type))
                        .toArray(Predicate[]::new)));
                break;
            case "docs":
                predicates.add(criteriaBuilder.not(criteriaBuilder.or(
                        Arrays.stream(imageTypes)
                                .map(type -> criteriaBuilder.like(root.get("type"), type))
                                .toArray(Predicate[]::new))));
                predicates.add(criteriaBuilder.not(criteriaBuilder.or(
                        Arrays.stream(videoTypes)
                                .map(type -> criteriaBuilder.like(root.get("type"), type))
                                .toArray(Predicate[]::new))));
                predicates.add(criteriaBuilder.not(criteriaBuilder.or(
                        Arrays.stream(pictoTypes)
                                .map(type -> criteriaBuilder.like(root.get("type"), type))
                                .toArray(Predicate[]::new))));
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
