package com.dcm.backend.repositories;

import com.dcm.backend.entities.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface LinkRepository extends JpaRepository<Link, String> {

    @Modifying
    @Query("delete from Link l where l.expirationDate < ?1")
        // Otherwise spring first select where e < i then delete links one by one
    void deleteAllByEpirationDateBefore(LocalDateTime instant);

}
