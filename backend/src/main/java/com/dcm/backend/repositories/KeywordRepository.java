package com.dcm.backend.repositories;

import com.dcm.backend.entities.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, String> {
}
