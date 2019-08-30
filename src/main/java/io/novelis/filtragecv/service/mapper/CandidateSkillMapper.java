package io.novelis.filtragecv.service.mapper;

import io.novelis.filtragecv.domain.*;
import io.novelis.filtragecv.service.dto.CandidateSkillDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link CandidateSkill} and its DTO {@link CandidateSkillDTO}.
 */
@Mapper(componentModel = "spring", uses = {SkillMapper.class, CandidateMapper.class})
public interface CandidateSkillMapper extends EntityMapper<CandidateSkillDTO, CandidateSkill> {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "candidate.id", target = "candidateId")
    CandidateSkillDTO toDto(CandidateSkill candidateSkill);

    @Mapping(source = "skillId", target = "skill")
    @Mapping(source = "candidateId", target = "candidate")
    CandidateSkill toEntity(CandidateSkillDTO candidateSkillDTO);

    default CandidateSkill fromId(Long id) {
        if (id == null) {
            return null;
        }
        CandidateSkill candidateSkill = new CandidateSkill();
        candidateSkill.setId(id);
        return candidateSkill;
    }
}
