package io.novelis.filtragecv.repository;

import io.novelis.filtragecv.domain.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Candidate entity.
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query(value = "select distinct candidate from Candidate candidate left join fetch candidate.cities left join fetch candidate.schools",
        countQuery = "select count(distinct candidate) from Candidate candidate")
    Page<Candidate> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct candidate from Candidate candidate left join fetch candidate.cities left join fetch candidate.schools")
    List<Candidate> findAllWithEagerRelationships();

    @Query("select candidate from Candidate candidate left join fetch candidate.cities left join fetch candidate.schools where candidate.id =:id")
    Optional<Candidate> findOneWithEagerRelationships(@Param("id") Long id);

}
