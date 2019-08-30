package io.novelis.filtragecv.repository;

import io.novelis.filtragecv.domain.SkillSynonym;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the SkillSynonym entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SkillSynonymRepository extends JpaRepository<SkillSynonym, Long> {
    Optional<SkillSynonym> findByName(String name);
}
