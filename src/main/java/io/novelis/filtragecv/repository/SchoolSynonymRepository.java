package io.novelis.filtragecv.repository;

import io.novelis.filtragecv.domain.SchoolSynonym;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the SchoolSynonym entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SchoolSynonymRepository extends JpaRepository<SchoolSynonym, Long> {
    Optional<SchoolSynonym> findByName(String name);
}
