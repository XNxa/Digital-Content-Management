package com.dcm.backend.repositories;

import com.dcm.backend.entities.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KeywordRepository extends JpaRepository<Keyword, String>,
        JpaSpecificationExecutor<Keyword> {
}
