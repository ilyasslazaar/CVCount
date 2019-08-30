package io.novelis.filtragecv.repository;

import io.novelis.filtragecv.domain.Candidate;
import io.novelis.filtragecv.domain.CandidateSkill;
import io.novelis.filtragecv.domain.Skill;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the CandidateSkill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {
    List<CandidateSkill> getAllBySkill(Skill skill);

    Optional<CandidateSkill> findCandidateSkillBySkillAndCandidate(Skill skill, Candidate candidate);
}
